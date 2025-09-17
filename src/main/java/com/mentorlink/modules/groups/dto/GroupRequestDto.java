package com.mentorlink.modules.groups.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupRequestDto {
    private String name;
    private List<Long> studentIds;  // students in group
    private Long facultyId;         // assigned faculty
}

