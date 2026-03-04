package com.mentorlink.modules.faculty.dto;

import com.mentorlink.modules.faculty.entity.FacultyMentorshipRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorshipRequestDto {
    private Long id;
    private Long groupId;
    private Long facultyId;
    private Long projectId;
    private String projectTopic;
    private String projectDescription;
    private FacultyMentorshipRequest.RequestStatus status;
}
