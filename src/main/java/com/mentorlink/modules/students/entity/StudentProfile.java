package com.mentorlink.modules.students.entity;

import com.mentorlink.modules.users.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore   // 🔑 prevent infinite recursion
    private User user;

    private String rollNumber;
    private String department;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;
}
