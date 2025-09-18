package com.mentorlink.modules.students.service;

import com.mentorlink.modules.students.dto.StudentProfileDTO;
import com.mentorlink.modules.students.entity.StudentProfile;
import com.mentorlink.modules.students.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    // ✅ Save profile from DTO
    public StudentProfileDTO saveProfile(StudentProfileDTO dto) {
        StudentProfile profile = StudentProfile.builder()
                .id(dto.getId())
                .department(dto.getDepartment())
                .rollNumber(dto.getRollNumber())
                .yearOfStudy(dto.getYearOfStudy())
                .build();

        StudentProfile saved = studentProfileRepository.save(profile);
        return convertToDTO(saved);
    }

    // ✅ Convert entity -> DTO
    public StudentProfileDTO convertToDTO(StudentProfile profile) {
        return StudentProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                .department(profile.getDepartment())
                .rollNumber(profile.getRollNumber())
                .yearOfStudy(profile.getYearOfStudy())
                .build();
    }

    // ✅ FIX: Get profile by userId
    public StudentProfileDTO getProfile(Long userId) {
        return studentProfileRepository.findByUserId(userId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));
    }
}