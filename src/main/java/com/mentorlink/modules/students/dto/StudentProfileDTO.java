package com.mentorlink.modules.students.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileDTO {

    private Long id;
    private Long userId;
    private String department;
    private String rollNumber;
    private String yearOfStudy;   // ✅ Correct field name
}
