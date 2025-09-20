package com.mentorlink.modules.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mentorlink.modules.faculty.dto.FacultyProfileDto;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
// hide nulls and empty collections/strings from JSON output
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String role;

    // student-specific (will be omitted when null/empty)
    private String rollNumber;
    private String department;
    private Integer yearOfStudy;
    private Set<String> skills;
    private Set<String> achievements;

    // optional faculty nested object placeholder (if you want)
    private FacultyProfileDto facultyProfile; // -> present only for FACULTY
}
