package com.mentorlink.modules.groups.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupResponseDto {
    private Long id;
    private String name;
    private Long projectId;
    private String joinToken;
    private String status;
    private List<String> studentNames;
    private Long facultyUserId;
}
