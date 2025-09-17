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

        user = userRepository.save(user);
        return toDto(user);
    }

    // ✅ Create new user (Admin use case)
    public UserResponseDto createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(role)
                .rollNumber(user.getRollNumber())
                .department(user.getDepartment())
                .yearOfStudy(user.getYearOfStudy())
                .skills(user.getSkills())
                .achievements(user.getAchievements())
                .build();
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
