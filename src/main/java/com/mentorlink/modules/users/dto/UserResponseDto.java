package com.mentorlink.modules.users.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String role;

    // 🎯 New fields
    private String rollNumber;
    private String department;
    private Integer yearOfStudy;
    private Set<String> skills;
    private Set<String> achievements;
}
