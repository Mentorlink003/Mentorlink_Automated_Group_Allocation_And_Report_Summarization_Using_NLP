package com.mentorlink.modules.projects.dto;

import lombok.Data;

@Data
public class ProjectRequestDto {
    private String title;
    private String description;
    private String domain;
    private String techStack;
    private Long groupId; // ✅ optional, if project should be linked to group
}
