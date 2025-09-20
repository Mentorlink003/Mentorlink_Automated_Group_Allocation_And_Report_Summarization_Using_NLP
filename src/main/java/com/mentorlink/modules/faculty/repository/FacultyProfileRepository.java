// src/main/java/com/mentorlink/modules/faculty/repository/FacultyProfileRepository.java
package com.mentorlink.modules.faculty.repository;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyProfileRepository extends JpaRepository<FacultyProfile, Long> {
    Optional<FacultyProfile> findByUserId(Long userId);
}
