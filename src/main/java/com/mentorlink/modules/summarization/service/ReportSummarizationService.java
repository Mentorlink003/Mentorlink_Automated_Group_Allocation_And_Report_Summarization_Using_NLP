package com.mentorlink.modules.summarization.service;

import com.mentorlink.common.exception.ApiException;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.groups.repository.GroupRepository;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.summarization.dto.ReportSummaryDto;
import com.mentorlink.modules.summarization.entity.ReportSummary;
import com.mentorlink.modules.summarization.repository.ReportSummaryRepository;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportSummarizationService {

    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ReportSummaryRepository reportSummaryRepository;
    private final FileStorageService fileStorageService;
    private final RestTemplate restTemplate;

    @Value("${app.nlp.summarization.url:http://localhost:5001}")
    private String nlpServiceUrl;

    /**
     * Upload PDF, send to NLP service, store summary.
     * Only group members can submit. Faculty and Admin can also trigger.
     */
    @Transactional
    public ReportSummaryDto summarizeReport(Long projectId, MultipartFile file, String submitterEmail, boolean isAdmin) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(org.springframework.http.HttpStatus.NOT_FOUND, "NOT_FOUND", "Project not found"));

        Group group = project.getGroup();
        if (group == null) {
            throw new ApiException(org.springframework.http.HttpStatus.BAD_REQUEST, "NO_GROUP", "Project has no group linked");
        }

        User submitter = userRepository.findByEmail(submitterEmail)
                .orElseThrow(() -> new ApiException(org.springframework.http.HttpStatus.NOT_FOUND, "NOT_FOUND", "User not found"));

        boolean isGroupMember = group.getMembers().stream().anyMatch(u -> u.getEmail().equals(submitterEmail));
        boolean isMentor = project.getMentor() != null && project.getMentor().getEmail().equals(submitterEmail);
        if (!isGroupMember && !isMentor && !isAdmin) {
            throw new ApiException(org.springframework.http.HttpStatus.FORBIDDEN, "FORBIDDEN",
                    "Only group members, faculty mentor, or admin can summarize reports");
        }

        if (file == null || file.isEmpty()) {
            throw new ApiException(org.springframework.http.HttpStatus.BAD_REQUEST, "EMPTY_FILE", "No file provided");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new ApiException(org.springframework.http.HttpStatus.BAD_REQUEST, "INVALID_FILE", "File must be a PDF");
        }

        // Save file
        String storedPath = fileStorageService.store(file, "reports/" + projectId);
        Path fullPath = fileStorageService.resolve(storedPath);

        try {
            // Call Python NLP service
            String summary = callNlpSummarizationService(fullPath);
            if (summary == null || summary.isBlank()) {
                throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "SUMMARIZATION_FAILED", "NLP service returned empty summary");
            }

            ReportSummary entity = ReportSummary.builder()
                    .project(project)
                    .submittedBy(submitter)
                    .reportFilePath(storedPath)
                    .generatedSummary(summary)
                    .originalFilename(filename)
                    .build();
            entity = reportSummaryRepository.save(entity);
            return ReportSummaryDto.from(entity);
        } catch (Exception e) {
            try {
                fileStorageService.delete(storedPath);
            } catch (IOException ignored) {}
            if (e instanceof ApiException) throw e;
            throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "SUMMARIZATION_FAILED", "Summarization failed: " + e.getMessage());
        }
    }

    private String callNlpSummarizationService(Path pdfPath) {
        String url = nlpServiceUrl.trim().replaceAll("/$", "") + "/summarize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(pdfPath.toFile()));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<NlpSummarizeResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    NlpSummarizeResponse.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String err = response.getBody().getError();
                if (err != null) throw new RuntimeException("NLP service error: " + err);
                return response.getBody().getSummary();
            }
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String bodyStr = e.getResponseBodyAsString();
            throw new RuntimeException("NLP service failed (" + e.getStatusCode() + "): " + bodyStr);
        } catch (Exception e) {
            throw new RuntimeException("NLP service unavailable: " + e.getMessage(), e);
        }
        return null;
    }

    public List<ReportSummaryDto> listByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(org.springframework.http.HttpStatus.NOT_FOUND, "NOT_FOUND", "Project not found"));
        return reportSummaryRepository.findByProjectOrderByCreatedAtDesc(project).stream()
                .map(ReportSummaryDto::from)
                .collect(Collectors.toList());
    }

    private static class NlpSummarizeResponse {
        private String summary;
        private String error;

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
