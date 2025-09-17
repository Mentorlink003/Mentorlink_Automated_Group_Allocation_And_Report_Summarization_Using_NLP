package com.mentorlink.modules.faculty;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.faculty.dto.FacultyRequest;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyProfile>> createFaculty(@RequestBody FacultyRequest dto) {
        FacultyProfile faculty = facultyService.createFaculty(dto);  // <-- dto is FacultyRequest
        return ResponseEntity.ok(ApiResponse.success(faculty));
    }
}
