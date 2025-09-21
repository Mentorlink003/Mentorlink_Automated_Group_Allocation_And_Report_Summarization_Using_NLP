package com.mentorlink.modules.groups.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponseDto {
    private Long id;
    private String name;
    private String joinToken;
    private Long leaderId;
    private Long projectId;
    private int memberCount;
}
