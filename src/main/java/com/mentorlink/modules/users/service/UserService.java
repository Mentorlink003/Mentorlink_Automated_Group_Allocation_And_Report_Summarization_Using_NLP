package com.mentorlink.modules.users.service;

import com.mentorlink.common.dto.ApiResponse;
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
        String role = extractRole(user);
        return new UserResponseDto(user.getId(), user.getEmail(), user.getFullName(), role);
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

        userRepository.save(user);

        String role = extractRole(user);
        return new UserResponseDto(user.getId(), user.getEmail(), user.getFullName(), role);
    }

    // ✅ Create new user (Admin use case)
    public User createUser(User user) {
        // Encode password before saving
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // ✅ Find user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // ✅ Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Delete user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
