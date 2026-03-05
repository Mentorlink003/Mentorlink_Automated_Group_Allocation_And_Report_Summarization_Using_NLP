package com.mentorlink.modules.faculty;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.faculty.dto.FacultyListDto;
import com.mentorlink.modules.faculty.dto.FacultyRequest;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.faculty.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;
    private final FacultyProfileRepository facultyProfileRepository;

    /**
     * List faculty. Use ?availableOnly=true to show only faculty with remaining slots (for mentor assignment).
     * Faculty with filled slots (currentLoad >= maxGroups) are closed – no one can request them as mentor.
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<FacultyListDto>>> listFaculty(
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        var stream = facultyProfileRepository.findAll().stream();
        if (availableOnly) {
            stream = stream.filter(f -> f.getCurrentLoad() < f.getMaxGroups());
        }
        List<FacultyListDto> list = stream
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
}
