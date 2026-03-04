package com.mentorlink.modules.submissions.repository;

import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.submissions.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByProject(Project project);
    List<Submission> findByProjectAndCategory(Project project, String category);
}
