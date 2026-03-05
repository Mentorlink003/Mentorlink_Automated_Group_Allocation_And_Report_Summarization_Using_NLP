package com.mentorlink.modules.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectSummaryDto {
    private Long projectId;
    private String title;
    private String description;
    private String domain;
    private int progress;
    private MentorSummaryDto mentor;
    private Long groupId;
}
