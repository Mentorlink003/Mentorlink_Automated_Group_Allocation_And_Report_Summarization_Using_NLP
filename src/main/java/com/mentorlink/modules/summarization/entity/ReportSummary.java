package com.mentorlink.modules.summarization.entity;

import com.mentorlink.common.auditing.Auditable;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_summaries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummary extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User submittedBy;

    @Column(name = "report_file_path", nullable = false)
    private String reportFilePath;

    @Column(name = "generated_summary", nullable = false, columnDefinition = "TEXT")
    private String generatedSummary;

    @Column(name = "original_filename")
    private String originalFilename;
}
