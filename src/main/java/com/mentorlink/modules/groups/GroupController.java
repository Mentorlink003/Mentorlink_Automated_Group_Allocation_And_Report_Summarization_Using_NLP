// src/main/java/com/mentorlink/modules/groups/GroupController.java
package com.mentorlink.modules.groups;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.faculty.dto.RequestMentorshipDto;
import com.mentorlink.modules.faculty.entity.FacultyMentorshipRequest;
import com.mentorlink.modules.faculty.service.FacultyMentorshipRequestService;
import com.mentorlink.modules.groups.dto.GroupRequestDto;
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
    private final FacultyMentorshipRequestService mentorshipRequestService;

    // ✅ Create group (leader is the logged-in student)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GroupResponseDto>> createGroup(@RequestBody GroupRequestDto dto,
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

    @PostMapping("/{groupId}/request-faculty")
    public ResponseEntity<ApiResponse<FacultyMentorshipRequest>> requestFaculty(
            @PathVariable Long groupId,
            @RequestBody RequestMentorshipDto dto,
            Authentication auth) {
        dto.setGroupId(groupId);
        return ResponseEntity.ok(ApiResponse.success(
                mentorshipRequestService.requestMentorship(
                        groupId, dto.getFacultyId(), dto.getProjectTopic(),
                        dto.getProjectDescription(), dto.getProjectId())));
    }
}
