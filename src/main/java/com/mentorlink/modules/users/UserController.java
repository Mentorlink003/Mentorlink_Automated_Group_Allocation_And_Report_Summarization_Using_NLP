package com.mentorlink.modules.users;

import com.mentorlink.modules.users.dto.UserResponseDto;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponseDto> updateProfile(@PathVariable Long id, @RequestBody UserResponseDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRollNumber(dto.getRollNumber());
        user.setDepartment(dto.getDepartment());
        user.setYearOfStudy(dto.getYearOfStudy());
        user.setSkills(dto.getSkills());
        user.setAchievements(dto.getAchievements());

        user = userRepository.save(user);

        return ResponseEntity.ok(
                UserResponseDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRoles().iterator().next())
                        .rollNumber(user.getRollNumber())
                        .department(user.getDepartment())
                        .yearOfStudy(user.getYearOfStudy())
                        .skills(user.getSkills())
                        .achievements(user.getAchievements())
                        .build()
        );
    }
}
