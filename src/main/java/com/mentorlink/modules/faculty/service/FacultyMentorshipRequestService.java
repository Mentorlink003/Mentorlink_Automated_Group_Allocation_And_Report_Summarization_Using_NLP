package com.mentorlink.modules.faculty.service;

import com.mentorlink.common.debug.AgentDebugLog;
import com.mentorlink.modules.faculty.entity.FacultyMentorshipRequest;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyMentorshipRequestRepository;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.groups.repository.GroupRepository;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyMentorshipRequestService {

    private final FacultyMentorshipRequestRepository requestRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Student group requests faculty mentorship with project details.
     */
    @Transactional
    public FacultyMentorshipRequest requestMentorship(Long groupId, Long facultyId, String projectTopic,
                                                     String projectDescription, Long projectId) {
        // #region agent log
        AgentDebugLog.log("99a5a7", "group-not-found", "H1",
                "FacultyMentorshipRequestService.java:requestMentorship",
                "Requesting mentorship", "{\"groupId\":" + groupId + ",\"facultyId\":" + facultyId + ",\"projectId\":" + projectId + "}");
        // #endregion

        boolean exists = groupRepository.existsById(groupId);
        // #region agent log
        AgentDebugLog.log("99a5a7", "group-not-found", "H1",
                "FacultyMentorshipRequestService.java:requestMentorship",
                "Group existsById result", "{\"groupId\":" + groupId + ",\"exists\":" + exists + "}");
        // #endregion

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        FacultyProfile faculty = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (faculty.getCurrentLoad() >= faculty.getMaxGroups()) {
            throw new RuntimeException("Faculty has reached max group limit");
        }

        Project project = group.getProject();
        if (project == null && projectId != null) {
            project = projectRepository.findById(projectId).orElse(null);
        }
        if (project == null) {
            project = Project.builder()
                    .title(projectTopic)
                    .description(projectDescription != null ? projectDescription : "")
                    .domain("General")
                    .techStack("TBD")
                    .progress(0)
                    .build();
            project = projectRepository.save(project);
            group.setProject(project);
            project.setGroup(group);
            groupRepository.save(group);
        } else {
            project.setTitle(projectTopic);
            if (projectDescription != null) project.setDescription(projectDescription);
            projectRepository.save(project);
        }

        FacultyMentorshipRequest req = FacultyMentorshipRequest.builder()
                .group(group)
                .faculty(faculty)
                .project(project)
                .projectTopic(projectTopic)
                .projectDescription(projectDescription)
                .status(FacultyMentorshipRequest.RequestStatus.PENDING)
                .requestedAt(Instant.now())
                .build();
        return requestRepository.save(req);
    }

    @Transactional
    public FacultyMentorshipRequest approve(Long requestId, String facultyEmail) {
        FacultyMentorshipRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (req.getStatus() != FacultyMentorshipRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }
        FacultyProfile faculty = facultyProfileRepository.findByEmail(facultyEmail)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        if (!req.getFaculty().getId().equals(faculty.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (faculty.getCurrentLoad() >= faculty.getMaxGroups()) {
            throw new RuntimeException("Faculty has reached max group limit");
        }

        req.setStatus(FacultyMentorshipRequest.RequestStatus.APPROVED);
        req.setRespondedAt(Instant.now());
        req.getProject().setMentor(faculty);
        faculty.setCurrentLoad(faculty.getCurrentLoad() + 1);
        requestRepository.save(req);
        projectRepository.save(req.getProject());
        facultyProfileRepository.save(faculty);
        return req;
    }

    @Transactional
    public FacultyMentorshipRequest reject(Long requestId, String facultyEmail) {
        FacultyMentorshipRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (req.getStatus() != FacultyMentorshipRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }
        FacultyProfile faculty = facultyProfileRepository.findByEmail(facultyEmail)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        if (!req.getFaculty().getId().equals(faculty.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        req.setStatus(FacultyMentorshipRequest.RequestStatus.REJECTED);
        req.setRespondedAt(Instant.now());
        return requestRepository.save(req);
    }

    public List<FacultyMentorshipRequest> getPendingForFaculty(String facultyEmail) {
        FacultyProfile faculty = facultyProfileRepository.findByEmail(facultyEmail)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        return requestRepository.findByFacultyAndStatus(faculty, FacultyMentorshipRequest.RequestStatus.PENDING);
    }
}
