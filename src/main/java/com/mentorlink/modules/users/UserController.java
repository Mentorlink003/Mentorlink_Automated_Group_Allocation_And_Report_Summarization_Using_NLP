package com.mentorlink.modules.users;

import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.dto.UserUpdateDto;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ Get current logged-in user (from JWT)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

    // ✅ Update profile of logged-in user
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateUser(
            Authentication authentication,
            @RequestBody UserUpdateDto update) {
        return ResponseEntity.ok(userService.updateUser(authentication, update));
    }

    // ✅ Admin creates a new user
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // ✅ Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserResponseDto>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ✅ Get all users
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Delete user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
