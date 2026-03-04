package com.mentorlink.modules.admin;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.admin.service.AdminExcelService;
import com.mentorlink.modules.admin.service.AnalyticsService;
import com.mentorlink.modules.deadlines.entity.Deadline;
import com.mentorlink.modules.deadlines.entity.DeadlineType;
import com.mentorlink.modules.deadlines.service.DeadlineService;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.service.RecommenderService;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final AdminExcelService adminExcelService;
    private final DeadlineService deadlineService;
    private final RecommenderService recommenderService;
    private final AnalyticsService analyticsService;

    // ========== Excel Upload ==========
    @PostMapping("/upload/students")
    public ResponseEntity<ApiResponse<AdminExcelService.ExcelUploadResult>> uploadStudents(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(adminExcelService.uploadStudents(file)));
    }

    @PostMapping("/upload/faculty")
    public ResponseEntity<ApiResponse<AdminExcelService.ExcelUploadResult>> uploadFaculty(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(adminExcelService.uploadFaculty(file)));
    }

    // ========== Deadline Management ==========
    @PostMapping("/deadlines")
    public ResponseEntity<ApiResponse<Object>> setDeadline(
            @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String typeStr = (String) body.get("type");
        String dueDateStr = (String) body.get("dueDate");
        DeadlineType type = DeadlineType.valueOf(typeStr);
        Instant dueDate = Instant.parse(dueDateStr);
        return ResponseEntity.ok(ApiResponse.success(deadlineService.createOrUpdate(name, dueDate, type)));
    }

    @GetMapping("/deadlines")
    public ResponseEntity<ApiResponse<List<Deadline>>> getDeadlines() {
        return ResponseEntity.ok(ApiResponse.success(deadlineService.getAll()));
    }

    // ========== Auto-Assignment ==========
    @PostMapping("/auto-group")
    public ResponseEntity<ApiResponse<List<Group>>> autoGroup() {
        return ResponseEntity.ok(ApiResponse.success(recommenderService.autoGroupStudents()));
    }

    @PostMapping("/auto-assign-faculty")
    public ResponseEntity<ApiResponse<Integer>> autoAssignFaculty() {
        return ResponseEntity.ok(ApiResponse.success(recommenderService.autoAssignFaculty()));
    }

    // ========== Manual Assignment ==========
    @PostMapping("/projects/{projectId}/assign/{facultyId}")
    public ResponseEntity<ApiResponse<String>> assignFacultyToProject(
            @PathVariable Long projectId,
            @PathVariable Long facultyId
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User facultyUser = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (!facultyUser.getRoles().contains("FACULTY")) {
            throw new RuntimeException("User is not a faculty");
        }

        FacultyProfile profile = facultyUser.getFacultyProfile();
        if (profile == null) {
            throw new RuntimeException("Faculty profile not created");
        }

        if (profile.getCurrentLoad() >= profile.getMaxGroups()) {
            throw new RuntimeException("Faculty already at max group load");
        }

        // ✅ assign faculty as project mentor
        project.setMentor(profile);
        projectRepository.save(project);

        // update faculty load
        profile.setCurrentLoad(profile.getCurrentLoad() + 1);
        facultyProfileRepository.save(profile);

        return ResponseEntity.ok(ApiResponse.success(
                "Faculty assigned successfully to project: " + project.getTitle()
        ));
    }

    @PostMapping("/projects/{projectId}/unassign")
    public ResponseEntity<ApiResponse<String>> unassignFaculty(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (project.getMentor() == null) {
            throw new RuntimeException("Project has no mentor");
        }
        var mentor = project.getMentor();
        mentor.setCurrentLoad(Math.max(0, mentor.getCurrentLoad() - 1));
        project.setMentor(null);
        projectRepository.save(project);
        facultyProfileRepository.save(mentor);
        return ResponseEntity.ok(ApiResponse.success("Faculty unassigned"));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> analytics() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getDashboard()));
    }

    @PutMapping("/faculty/{facultyId}/max-groups")
    public ResponseEntity<ApiResponse<FacultyProfile>> setFacultyMaxGroups(
            @PathVariable Long facultyId,
            @RequestBody Map<String, Integer> body) {
        FacultyProfile faculty = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        int max = body.getOrDefault("maxGroups", 3);
        faculty.setMaxGroups(Math.max(1, Math.min(10, max)));
        return ResponseEntity.ok(ApiResponse.success(facultyProfileRepository.save(faculty)));
    }
}
