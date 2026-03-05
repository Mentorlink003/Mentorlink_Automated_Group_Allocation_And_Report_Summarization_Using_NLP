// src/main/java/com/mentorlink/modules/groups/GroupController.java
package com.mentorlink.modules.groups;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.groups.dto.GroupRequestDto;
import jakarta.validation.Valid;
import com.mentorlink.modules.groups.dto.GroupResponseDto;
import com.mentorlink.modules.groups.service.GroupService;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponseDto>> getGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.success(groupService.getById(groupId)));
    }

    // ✅ Create group (leader is the logged-in student)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GroupResponseDto>> createGroup(@Valid @RequestBody GroupRequestDto dto,
                                                                     Authentication authentication) {
        String email = authentication.getName(); // email comes from JWT
        User leader = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        GroupResponseDto group = groupService.createGroup(dto, leader.getId());
        return ResponseEntity.ok(ApiResponse.success(group));
    }

    // ✅ Join group using token
    @PostMapping("/join/{token}")
    public ResponseEntity<ApiResponse<GroupResponseDto>> joinGroup(@PathVariable String token,
                                                                   Authentication authentication) {
        String email = authentication.getName(); // email comes from JWT
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        GroupResponseDto group = groupService.joinGroup(token, student.getId());
        return ResponseEntity.ok(ApiResponse.success(group));
    }

    // ✅ Faculty join as mentor using mentor token
    @PostMapping("/mentor/join/{token}")
    public ResponseEntity<ApiResponse<GroupResponseDto>> mentorJoin(@PathVariable String token,
                                                                    Authentication authentication) {
        String email = authentication.getName();
        User facultyUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (!facultyUser.getRoles().contains("FACULTY")) {
            throw new RuntimeException("Only faculty can join as mentor");
        }

        var group = groupService.mentorJoinByToken(token, email);
        return ResponseEntity.ok(ApiResponse.success(groupService.getById(group.getId())));
    }
}
