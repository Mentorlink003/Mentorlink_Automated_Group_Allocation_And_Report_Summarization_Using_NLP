package com.mentorlink.modules.groups.dto;

import lombok.Data;

@Data
public class GroupRequestDto {
    private String name;
    private Long projectId; // link to project when creating
}
