package com.mentorlink.modules.auth.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    private String email;
    private String fullName;
    private String password;
    private String role;

    // 🎯 New fields
    private String rollNumber;
    private String department;
    private Integer yearOfStudy;
    private Set<String> skills;
    private Set<String> achievements;
}
