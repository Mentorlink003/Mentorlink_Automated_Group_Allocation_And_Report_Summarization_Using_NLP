package com.mentorlink.modules.students.dto;

import lombok.Data;
import java.util.Set;

@Data
public class StudentProfileDTO {
    private Long id;
    private Long userId;
    private String rollNumber;
    private String department;
    private String year;
    private Set<String> skills;
}
