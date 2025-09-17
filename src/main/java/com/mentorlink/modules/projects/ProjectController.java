package com.mentorlink.modules.projects;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.projects.dto.ProjectDTO;
import com.mentorlink.modules.projects.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> create(@RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(projectService.createProject(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(projectService.getAllProjects()));
    }

    @PostMapping("/join/{token}")
    public ResponseEntity<ApiResponse<ProjectDTO>> joinByToken(@PathVariable String token) {
        return ResponseEntity.ok(ApiResponse.success(projectService.joinProjectByToken(token)));
    }

    // ✅ Faculty updates progress
    @PutMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProgress(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Integer progress) {
        return ResponseEntity.ok(ApiResponse.success(
                projectService.updateProjectProgress(id, status, progress)
        ));
    }
}
