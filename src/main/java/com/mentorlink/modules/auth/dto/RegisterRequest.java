package com.mentorlink.modules.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String fullName;   // ✅ add this
    private String password;
    private String role;       // ✅ add this
}
