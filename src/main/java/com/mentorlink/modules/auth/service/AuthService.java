package com.mentorlink.modules.auth.service;

import com.mentorlink.modules.auth.dto.LoginRequest;
import com.mentorlink.modules.auth.dto.RegisterRequest;
import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Register new user
    public ApiResponse<UserResponseDto> register(RegisterRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + dto.getEmail());
        }

        User user = User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        user = userRepository.save(user);

        List<String> skills = user.getSkills() == null ? List.of() : new ArrayList<>(user.getSkills());
        List<String> achievements = user.getAchievements() == null ? List.of() : new ArrayList<>(user.getAchievements());

        return ApiResponse.success(
                UserResponseDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(dto.getRole())  // use role from registration
                        .rollNumber(user.getRollNumber())
                        .department(user.getDepartment())
                        .yearOfStudy(user.getYearOfStudy())
                        .skills(skills)
                        .achievements(achievements)
                        .build()
        );
    }

    // ✅ Login user
    public ApiResponse<String> login(LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // In real system → generate JWT, here return a dummy token for now
        return ApiResponse.success("dummy-jwt-token-for-" + user.getEmail());
    }
}
