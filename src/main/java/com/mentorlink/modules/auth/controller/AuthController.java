package com.mentorlink.modules.auth.controller;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.auth.dto.LoginRequest;
import com.mentorlink.modules.auth.dto.RegisterRequest;
import com.mentorlink.modules.auth.service.AuthService;
import com.mentorlink.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.dto.UserUpdateDto;
import com.mentorlink.modules.users.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final UserService userService;


    // ✅ Register endpoint
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // ✅ Login endpoint
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Extract role(s)
        List<String> roles = auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        String token = jwtTokenProvider.generate(auth.getName(), roles);

        return ResponseEntity.ok(ApiResponse.success(token));
    }
    // NEW: get current user
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> me(Authentication authentication) {
        UserResponseDto dto = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    // NEW: update current user (name and/or password)
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(Authentication authentication,
                                                               @RequestBody UserUpdateDto updateDto) {
        UserResponseDto updated = userService.updateUser(authentication, updateDto);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
}
