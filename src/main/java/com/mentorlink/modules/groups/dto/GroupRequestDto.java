package com.mentorlink.modules.groups.dto;

import lombok.Data;

@Data
public class GroupRequestDto {
    private String name;
    private Long projectId;       // optional: use existing project
    private String projectTitle; // optional: create new project if projectId null
    private String projectDescription;
}
