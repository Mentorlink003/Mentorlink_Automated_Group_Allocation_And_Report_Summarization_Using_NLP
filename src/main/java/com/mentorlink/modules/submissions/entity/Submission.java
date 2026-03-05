package com.mentorlink.modules.submissions.entity;

import com.mentorlink.common.auditing.Auditable;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "category"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Submission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false, length = 50)
    private String category; // REPORT, RESEARCH_PAPER, PPT

    @Column(name = "original_filename")
    private String originalFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id")
    private User submittedBy;
}
