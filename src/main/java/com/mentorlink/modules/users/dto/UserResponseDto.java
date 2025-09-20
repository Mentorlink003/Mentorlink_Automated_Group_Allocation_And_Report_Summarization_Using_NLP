package com.mentorlink.modules.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private String rollNumber;
    private String department;
    private Integer yearOfStudy;   // 🔹 FIX: must be Integer
    private List<String> skills;
    private List<String> achievements;
}
