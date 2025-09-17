package com.mentorlink.modules.projects.service;

import com.mentorlink.modules.projects.ProjectRepository;
import com.mentorlink.modules.projects.dto.ProjectDTO;
import com.mentorlink.modules.projects.dto.ProjectStudentGroupRequest;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private static final int MAX_STUDENTS = 3;

    // ✅ Create new project
    public ProjectDTO createProject(ProjectDTO dto) {
        Project project = Project.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .domain(dto.getDomain())
                .techStack(dto.getTechStack())
                .status(dto.getStatus() == null ? "NOT_STARTED" : dto.getStatus())
                .progress(0)
                .joinToken(UUID.randomUUID().toString().substring(0, 8))
                .build();

        project = projectRepository.save(project);

        return new ProjectDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getStatus(),
                project.getProgress(),
                project.getDomain(),
                project.getTechStack(),
                project.getJoinToken()
        );
    }

    // ✅ Get all projects
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(p -> new ProjectDTO(
                        p.getId(),
                        p.getTitle(),
                        p.getDescription(),
                        p.getStatus(),
                        p.getProgress(),
                        p.getDomain(),
                        p.getTechStack(),
                        p.getJoinToken()
                ))
                .collect(Collectors.toList());
    }

    // ✅ Student joins project by token
    @Transactional
    public ProjectDTO joinProjectByToken(String token) {
        Project project = projectRepository.findByJoinToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid project token"));

        if (project.getStudents() == null) {
            project.setStudents(new java.util.HashSet<>());
        }

        if (project.getStudents().size() >= MAX_STUDENTS) {
            throw new RuntimeException("Group is already full (max " + MAX_STUDENTS + ")");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        boolean inAnother = projectRepository.findAll().stream()
                .anyMatch(p -> p.getStudents() != null && p.getStudents().contains(student));
        if (inAnother) {
            throw new RuntimeException("Student is already part of another project");
        }

        project.getStudents().add(student);
        project = projectRepository.save(project);

        return new ProjectDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getStatus(),
                project.getProgress(),
                project.getDomain(),
                project.getTechStack(),
                project.getJoinToken()
        );
    }

    // ✅ Faculty updates project progress
    @Transactional
    public ProjectDTO updateProjectProgress(Long projectId, String status, Integer progress) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User faculty = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!faculty.getRoles().contains("FACULTY")) {
            throw new RuntimeException("Only faculty can update progress");
        }

        project.setStatus(status);
        project.setProgress(progress != null ? progress : project.getProgress());
        project = projectRepository.save(project);

        return new ProjectDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getStatus(),
                project.getProgress(),
                project.getDomain(),
                project.getTechStack(),
                project.getJoinToken()
        );
    }
}
