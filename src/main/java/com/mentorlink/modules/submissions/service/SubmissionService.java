package com.mentorlink.modules.submissions.service;

import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.submissions.entity.Submission;
import com.mentorlink.modules.submissions.repository.SubmissionRepository;
import com.mentorlink.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;

    public Submission upload(Long projectId, MultipartFile file, String category) throws java.io.IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        String path = fileStorageService.store(file, "submissions/" + projectId);
        return submissionRepository.save(Submission.builder()
                .project(project)
                .filePath(path)
                .category(category != null ? category : "Assignment")
                .build());
    }

    public List<Submission> listByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return submissionRepository.findByProject(project);
    }
}
