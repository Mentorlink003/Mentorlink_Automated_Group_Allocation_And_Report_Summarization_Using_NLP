// src/main/java/com/mentorlink/modules/auth/controller/AuthController.java
package com.mentorlink.modules.auth.controller;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.auth.dto.*;
import com.mentorlink.modules.auth.service.AuthService;
import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.dto.UserUpdateDto;
import com.mentorlink.modules.users.service.UserService;
import com.mentorlink.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // ✅ Register Student
    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerStudent(@RequestBody RegisterStudentRequest request) {
        return ResponseEntity.ok(authService.registerStudent(request));
    }

    // ✅ Register Faculty
    @PostMapping("/register/faculty")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerFaculty(@RequestBody RegisterFacultyRequest request) {
        return ResponseEntity.ok(authService.registerFaculty(request));
    }

    // ✅ Register Admin
    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerAdmin(@RequestBody RegisterAdminRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    // ✅ Login and generate JWT
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // ✅ Keep full authorities (ROLE_ADMIN, ROLE_FACULTY, ROLE_STUDENT)
        List<String> roles = auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority()) // no replace()
                .toList();

        // ✅ Generate JWT with full ROLE_ prefix
        String token = jwtTokenProvider.generate(auth.getName(), roles);

        return ResponseEntity.ok(ApiResponse.success(token));
    }

    // ✅ Get current logged-in user
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> me(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentUser(authentication)));
    }

    // ✅ Update user profile
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(Authentication authentication,
                                                               @RequestBody UserUpdateDto update) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(authentication, update)));
    }
}
