package com.mentorlink.modules.groups.service;

import com.mentorlink.modules.groups.dto.GroupRequestDto;
import com.mentorlink.modules.groups.dto.GroupResponseDto;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.groups.repository.GroupRepository;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ✅ Create a group with leader + project
    public GroupResponseDto createGroup(GroupRequestDto dto, Long leaderId) {
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader (student) not found"));

        Project project;
        if (dto.getProjectId() != null) {
            if (groupRepository.existsByProjectId(dto.getProjectId())) {
                throw new RuntimeException("This project already has a group. Use the join token instead.");
            }
            project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        } else {
            project = Project.builder()
                    .title(dto.getProjectTitle() != null ? dto.getProjectTitle() : "New Project")
                    .description(dto.getProjectDescription() != null ? dto.getProjectDescription() : "")
                    .domain("General")
                    .techStack("TBD")
                    .progress(0)
                    .build();
            project = projectRepository.save(project);
        }

        Group group = Group.builder()
                .name(dto.getName())
                .project(project)
                .leader(leader)
                .joinToken(UUID.randomUUID().toString())
                .build();

        group.getMembers().add(leader); // ✅ add leader as member

        Group saved = groupRepository.save(group);

        return mapToResponse(saved);
    }

    // ✅ Join group using token
    public GroupResponseDto joinGroup(String token, Long studentId) {
        Group group = groupRepository.findByJoinToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        group.getMembers().add(student);
        Group saved = groupRepository.save(group);

        return mapToResponse(saved);
    }

    // ✅ Mapper
    private GroupResponseDto mapToResponse(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .joinToken(group.getJoinToken())
                .projectId(group.getProject().getId())
                .leaderId(group.getLeader().getId())
                .memberCount(group.getMembers().size())
                .build();
    }
}
