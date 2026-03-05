package com.mentorlink.modules.groups.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponseDto {
    private Long id;
    private String name;
    private String joinToken;
    private String mentorJoinToken;
    private Long leaderId;
    private Long projectId;
    private String projectTitle;    // linked project title
    private String projectDescription;
    private int memberCount;
}
