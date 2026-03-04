package com.mentorlink.modules.users.service;

import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.dto.UserUpdateDto;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Get currently logged-in user details
    public UserResponseDto getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(user);
    }

    // ✅ Update current user profile
    public UserResponseDto updateUser(Authentication authentication, UserUpdateDto update) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (update.getFullName() != null && !update.getFullName().isBlank()) {
            user.setFullName(update.getFullName());
        }
        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(update.getPassword()));
        }
        if (update.getSkills() != null) {
            user.setSkills(new java.util.HashSet<>(update.getSkills()));
        }

        user = userRepository.save(user);
        return toDto(user);
    }

    // ✅ Find user by ID
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::toDto);
    }

    // ✅ Get all users
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Delete user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // 🔹 Convert User → DTO
    private UserResponseDto toDto(User user) {
        String role = extractRole(user);

        UserResponseDto.UserResponseDtoBuilder builder = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(role)
                .skills(user.getSkills() != null ? List.copyOf(user.getSkills()) : List.of())
                .achievements(user.getAchievements() != null ? List.copyOf(user.getAchievements()) : List.of());

        // 🔹 If Student → pull from StudentProfile
        if ("STUDENT".equals(role) && user.getStudentProfile() != null) {
            builder.rollNumber(user.getStudentProfile().getRollNumber());
            builder.department(user.getStudentProfile().getDepartment());
            try {
                builder.yearOfStudy(Integer.valueOf(user.getStudentProfile().getYearOfStudy()));
            } catch (NumberFormatException e) {
                builder.yearOfStudy(null);
            }
        }

        // 🔹 If Faculty → pull from FacultyProfile
        if ("FACULTY".equals(role) && user.getFacultyProfile() != null) {
            builder.department(user.getFacultyProfile().getDepartment());
        }

        // 🔹 If Admin → no extra details
        return builder.build();
    }


    // ✅ Extract role safely
    private String extractRole(User user) {
        try {
            if (user.getRoles() == null || user.getRoles().isEmpty()) return "STUDENT";
            Iterator<?> it = user.getRoles().iterator();
            Object first = it.next();
            if (first instanceof String) return (String) first;
            try {
                Method m = first.getClass().getMethod("getName");
                Object name = m.invoke(first);
                if (name != null) return name.toString();
            } catch (NoSuchMethodException ignored) {}
            return first.toString();
        } catch (Exception e) {
            return "STUDENT";
        }
    }
}
