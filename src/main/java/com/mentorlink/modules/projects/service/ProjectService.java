package com.mentorlink.modules.projects.service;

import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.common.exception.ApiException;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.projects.dto.ProjectRequestDto;
import com.mentorlink.modules.projects.dto.ProjectResponseDto;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.groups.repository.GroupRepository;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    public static final int MAX_GROUP_MEMBERS = 3;

    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final UserRepository userRepository;

    public ProjectResponseDto getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Project not found"));
        Group g = project.getGroup();
        return ProjectResponseDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .domain(project.getDomain())
                .techStack(project.getTechStack())
                .progress(project.getProgress())
                .groupId(g != null ? g.getId() : null)
                .joinToken(g != null ? g.getJoinToken() : null)
                .mentorJoinToken(g != null ? g.getMentorJoinToken() : null)
                .build();
    }

    public Project updateProgress(Long projectId, int progress, String facultyEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (project.getMentor() == null) {
            throw new RuntimeException("Project has no mentor assigned");
        }
        if (!project.getMentor().getEmail().equals(facultyEmail)) {
            throw new RuntimeException("Only assigned faculty can update progress");
        }
        project.setProgress(progress);
        return projectRepository.save(project);
    }

    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto dto, String creatorEmail) {
        User leader = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "User not found"));

        Project project = Project.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .domain(dto.getDomain())
                .techStack(dto.getTechStack())
                .build();
        project = projectRepository.save(project);

        // ✅ Auto-create group: creator becomes leader, others can join via joinToken (max 3 members)
        Group group = Group.builder()
                .name("Group: " + dto.getTitle())
                .project(project)
                .leader(leader)
                .joinToken(UUID.randomUUID().toString())
                .mentorJoinToken(UUID.randomUUID().toString())
                .build();
        group.getMembers().add(leader);
        project.setGroup(group);
        group = groupRepository.save(group);

        return ProjectResponseDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .domain(project.getDomain())
                .techStack(project.getTechStack())
                .progress(project.getProgress())
                .groupId(group.getId())
                .joinToken(group.getJoinToken())
                .mentorJoinToken(group.getMentorJoinToken())
                .build();
    }
}
