package com.mentorlink.modules.projects.dto;

import com.mentorlink.modules.projects.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String title;
    private String description;
    private String domain;
    private String techStack;
    private ProjectStatus status;
    private String mentorName;
}
