package com.mentorlink.modules.students.service;

import com.mentorlink.modules.students.StudentProfileRepository;
import com.mentorlink.modules.students.dto.StudentProfileDTO;
import com.mentorlink.modules.students.entity.StudentProfile;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public StudentProfileDTO saveProfile(StudentProfileDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(dto.getUserId())
                .orElse(StudentProfile.builder().user(user).build());

        profile.setRollNumber(dto.getRollNumber());
        profile.setDepartment(dto.getDepartment());
        profile.setYear(dto.getYear());
        profile.setSkills(dto.getSkills());

        StudentProfile saved = studentProfileRepository.save(profile);

        dto.setId(saved.getId());
        return dto;
    }

    public StudentProfileDTO getProfile(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setRollNumber(profile.getRollNumber());
        dto.setDepartment(profile.getDepartment());
        dto.setYear(profile.getYear());
        dto.setSkills(profile.getSkills());

        return dto;
    }
}
