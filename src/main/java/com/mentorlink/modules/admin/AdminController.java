package com.mentorlink.modules.admin;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.projects.ProjectRepository;
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

    // ✅ Assign faculty to project
    @PostMapping("/projects/{projectId}/assign/{facultyId}")
    public ResponseEntity<String> assignFacultyToProject(
            @PathVariable Long projectId,
            @PathVariable Long facultyId
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (!faculty.getRoles().contains("FACULTY")) {
            throw new RuntimeException("User is not a faculty");
        }

        if (faculty.getFacultyProfile() == null) {
            throw new RuntimeException("Faculty profile not created");
        }

        FacultyProfile profile = faculty.getFacultyProfile();

        if (profile.getCurrentLoad() >= profile.getMaxGroups()) {
            throw new RuntimeException("Faculty already at max group load");
        }

        // assign faculty to project
        project.setMentor(profile);
        projectRepository.save(project);

        // update faculty load
        profile.setCurrentLoad(profile.getCurrentLoad() + 1);
        facultyProfileRepository.save(profile);

        return ResponseEntity.ok("Faculty assigned successfully to project: " + project.getTitle());
    }
}
