package com.mentorlink.modules.groups;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.groups.dto.GroupRequestDto;
import com.mentorlink.modules.groups.entity.Group;
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

    // ✅ Create group (leader = logged in student)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Group>> createGroup(@RequestBody GroupRequestDto dto,
                                                          Authentication authentication) {
        String email = authentication.getName(); // logged-in user’s email
        User leader = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.createGroup(dto, leader.getId());
        return ResponseEntity.ok(ApiResponse.success(group));
    }

    // ✅ Join group using token
    @PostMapping("/join/{token}")
    public ResponseEntity<ApiResponse<Group>> joinGroup(@PathVariable String token,
                                                        Authentication authentication) {
        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.joinGroup(token, student.getId());
        return ResponseEntity.ok(ApiResponse.success(group));
    }
}
