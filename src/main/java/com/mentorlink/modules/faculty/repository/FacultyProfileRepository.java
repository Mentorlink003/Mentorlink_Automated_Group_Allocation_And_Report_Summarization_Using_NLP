package com.mentorlink.modules.faculty.repository;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyProfileRepository extends JpaRepository<FacultyProfile, Long> {
}
