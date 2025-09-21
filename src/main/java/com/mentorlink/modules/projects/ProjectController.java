package com.mentorlink.modules.projects;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.projects.dto.ProjectRequestDto;
import com.mentorlink.modules.projects.dto.ProjectResponseDto;
import com.mentorlink.modules.projects.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> create(@RequestBody ProjectRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(projectService.createProject(dto)));
    }
}
