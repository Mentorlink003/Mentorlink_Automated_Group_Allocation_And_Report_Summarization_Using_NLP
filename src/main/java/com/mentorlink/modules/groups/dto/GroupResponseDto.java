package com.mentorlink.modules.groups.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDto {
    private Long id;
    private String name;
    private List<String> students; // list of student emails/names
    private String faculty;        // faculty name
    private String projectTitle;   // if assigned
}
