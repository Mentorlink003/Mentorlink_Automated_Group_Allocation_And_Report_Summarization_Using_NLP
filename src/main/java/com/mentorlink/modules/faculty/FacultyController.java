package com.mentorlink.modules.faculty;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.faculty.dto.FacultyListDto;
import com.mentorlink.modules.faculty.dto.FacultyRequest;
import com.mentorlink.modules.faculty.dto.RequestMentorshipDto;
import com.mentorlink.modules.faculty.entity.FacultyMentorshipRequest;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.faculty.service.FacultyMentorshipRequestService;
import com.mentorlink.modules.faculty.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;
    private final FacultyMentorshipRequestService mentorshipRequestService;
    private final FacultyProfileRepository facultyProfileRepository;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<FacultyListDto>>> listFaculty() {
        List<FacultyListDto> list = facultyProfileRepository.findAll().stream()
                .map(f -> FacultyListDto.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .email(f.getEmail())
                        .department(f.getDepartment())
                        .expertise(f.getExpertise())
                        .currentLoad(f.getCurrentLoad())
                        .maxGroups(f.getMaxGroups())
                        .available(f.getCurrentLoad() < f.getMaxGroups())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyProfile>> createFaculty(@RequestBody FacultyRequest dto) {
        FacultyProfile faculty = facultyService.createFaculty(dto);
        return ResponseEntity.ok(ApiResponse.success(faculty));
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<FacultyMentorshipRequest>>> getPendingRequests(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                mentorshipRequestService.getPendingForFaculty(auth.getName())));
    }

    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<FacultyMentorshipRequest>> approve(@PathVariable Long requestId, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                mentorshipRequestService.approve(requestId, auth.getName())));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<FacultyMentorshipRequest>> reject(@PathVariable Long requestId, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                mentorshipRequestService.reject(requestId, auth.getName())));
    }
}
