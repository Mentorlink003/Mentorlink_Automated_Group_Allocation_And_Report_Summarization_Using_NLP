package com.mentorlink.modules.summarization.dto;

import com.mentorlink.modules.summarization.entity.ReportSummary;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReportSummaryDto {
    private Long id;
    private Long projectId;
    private Long studentId;
    private String reportFilePath;
    private String originalFilename;
    private String generatedSummary;
    private Instant createdAt;

    public static ReportSummaryDto from(ReportSummary s) {
        return ReportSummaryDto.builder()
                .id(s.getId())
                .projectId(s.getProject().getId())
                .studentId(s.getSubmittedBy() != null ? s.getSubmittedBy().getId() : null)
                .reportFilePath(s.getReportFilePath())
                .originalFilename(s.getOriginalFilename())
                .generatedSummary(s.getGeneratedSummary())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
