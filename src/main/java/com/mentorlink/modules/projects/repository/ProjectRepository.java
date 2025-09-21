package com.mentorlink.modules.projects.repository;

import com.mentorlink.modules.projects.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
