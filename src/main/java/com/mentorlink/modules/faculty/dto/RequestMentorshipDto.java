package com.mentorlink.modules.faculty.dto;

import lombok.Data;

@Data
public class RequestMentorshipDto {
    private Long groupId;
    private Long facultyId;
    private String projectTopic;
    private String projectDescription;
    private Long projectId; // optional, create new if null
}
