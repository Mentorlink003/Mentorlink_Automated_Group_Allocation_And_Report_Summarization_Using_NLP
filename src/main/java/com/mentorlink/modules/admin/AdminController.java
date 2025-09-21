package com.mentorlink.modules.admin;

import com.mentorlink.common.dto.ApiResponse;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FacultyProfileRepository facultyProfileRepository;

    // ✅ Assign faculty (as mentor) to project
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
}
