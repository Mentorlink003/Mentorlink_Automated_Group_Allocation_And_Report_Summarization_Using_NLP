package com.mentorlink.modules.auth.service;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.auth.dto.RegisterRequest;
import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ApiResponse<UserResponseDto> register(RegisterRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())   // ✅ make sure this is included
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(new HashSet<>(Set.of(dto.getRole())))
                .build();

        user = userRepository.save(user);

        return ApiResponse.success(
                new UserResponseDto(user.getId(), user.getEmail(), user.getFullName(), dto.getRole())
        );
    }


    // ✅ Login user
    public String login(RegisterRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtTokenProvider.generate(
                user.getEmail(),
                List.copyOf(user.getRoles()) // ✅ now uses Set<String>
        );
    }
}
