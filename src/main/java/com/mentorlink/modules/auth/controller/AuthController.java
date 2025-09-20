// src/main/java/com/mentorlink/modules/auth/controller/AuthController.java
package com.mentorlink.modules.auth.controller;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.auth.dto.*;
import com.mentorlink.modules.auth.service.AuthService;
import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.dto.UserUpdateDto;
import com.mentorlink.modules.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerStudent(@RequestBody RegisterStudentRequest request) {
        return ResponseEntity.ok(authService.registerStudent(request));
    }

    @PostMapping("/register/faculty")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerFaculty(@RequestBody RegisterFacultyRequest request) {
        return ResponseEntity.ok(authService.registerFaculty(request));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerAdmin(@RequestBody RegisterAdminRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> me(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentUser(authentication)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(Authentication authentication, @RequestBody UserUpdateDto update) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(authentication, update)));
    }
}
