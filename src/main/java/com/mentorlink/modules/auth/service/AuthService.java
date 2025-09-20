package com.mentorlink.modules.auth.service;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.auth.dto.LoginRequest;
import com.mentorlink.modules.auth.dto.RegisterAdminRequest;
import com.mentorlink.modules.auth.dto.RegisterFacultyRequest;
import com.mentorlink.modules.auth.dto.RegisterStudentRequest;
import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.students.entity.StudentProfile;
import com.mentorlink.modules.students.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Register Student
    public ApiResponse<UserResponseDto> registerStudent(RegisterStudentRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + dto.getEmail());
        }

        User user = User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        user.getRoles().add("STUDENT");
        user = userRepository.save(user);

        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .rollNumber(dto.getRollNumber())
                .department(dto.getDepartment())
                .yearOfStudy(dto.getYearOfStudy())
                .build();
        studentProfileRepository.save(profile);

        return ApiResponse.success(UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role("STUDENT")
                .rollNumber(profile.getRollNumber())
                .department(profile.getDepartment())
                .yearOfStudy(profile.getYearOfStudy())
                .skills(user.getSkills() != null ? List.copyOf(user.getSkills()) : List.of())
                .achievements(user.getAchievements() != null ? List.copyOf(user.getAchievements()) : List.of())
                .build());
    }

    // ✅ Register Faculty
    public ApiResponse<UserResponseDto> registerFaculty(RegisterFacultyRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + dto.getEmail());
        }

        User user = User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        user.getRoles().add("FACULTY");
        user = userRepository.save(user);

        FacultyProfile profile = FacultyProfile.builder()
                .user(user)
                .name(dto.getFullName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .expertise(dto.getExpertise())
                .maxGroups(dto.getMaxGroups())
                .build();
        facultyProfileRepository.save(profile);

        return ApiResponse.success(UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role("FACULTY")
                .department(profile.getDepartment())
                .skills(user.getSkills() != null ? List.copyOf(user.getSkills()) : List.of())
                .achievements(user.getAchievements() != null ? List.copyOf(user.getAchievements()) : List.of())
                .build());
    }

    // ✅ Register Admin
    public ApiResponse<UserResponseDto> registerAdmin(RegisterAdminRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + dto.getEmail());
        }

        User user = User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        user.getRoles().add("ADMIN");
        user = userRepository.save(user);

        return ApiResponse.success(UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role("ADMIN")
                .skills(user.getSkills() != null ? List.copyOf(user.getSkills()) : List.of())
                .achievements(user.getAchievements() != null ? List.copyOf(user.getAchievements()) : List.of())
                .build());
    }

    // ✅ Login User
    public ApiResponse<String> login(LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // ⚡ TODO: Replace with JWT later
        return ApiResponse.success("dummy-jwt-token-for-" + user.getEmail());
    }
}
