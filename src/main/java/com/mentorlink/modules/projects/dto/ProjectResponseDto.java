package com.mentorlink.modules.projects.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponseDto {
    private Long id;
    private String title;
    private String description;
    private String domain;
    private String techStack;
    private Long groupId; // ✅ linked group
}
