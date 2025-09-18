package com.mentorlink.modules.students.entity;

import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Project group;

    private String department;

    private String rollNumber;

    @Column(name = "year_of_study")
    private String yearOfStudy;

    // 🔹 Bridge methods for backward compatibility
    public String getYear() {
        return this.yearOfStudy;
    }

    public void setYear(String year) {
        this.yearOfStudy = year;
    }
}
