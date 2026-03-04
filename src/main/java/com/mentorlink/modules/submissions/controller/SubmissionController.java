package com.mentorlink.modules.submissions.controller;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.submissions.entity.Submission;
import com.mentorlink.modules.submissions.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Submission>> upload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "Assignment") String category) throws java.io.IOException {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.upload(projectId, file, category)));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<Submission>>> list(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.listByProject(projectId)));
    }
}
