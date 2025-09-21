package com.mentorlink.modules.projects.service;

import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.projects.dto.ProjectRequestDto;
import com.mentorlink.modules.projects.dto.ProjectResponseDto;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.groups.repository.GroupRepository;
import com.mentorlink.modules.groups.entity.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository; // ✅ inject group repo to fetch groups

    public ProjectResponseDto createProject(ProjectRequestDto dto) {
        Project project = Project.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .domain(dto.getDomain())
                .techStack(dto.getTechStack())
                .build();

        // ✅ If groupId is provided, link the project to that group
        if (dto.getGroupId() != null) {
            Group group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            project.setGroup(group);
        }

        project = projectRepository.save(project);

        return ProjectResponseDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .domain(project.getDomain())
                .techStack(project.getTechStack())
                .groupId(project.getGroup() != null ? project.getGroup().getId() : null)
                .build();
    }
}
