package com.mentorlink.modules.faculty.entity;

import com.mentorlink.modules.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faculty_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String department;
    private String expertise;

    @Column(name = "current_load", nullable = false)
    private Integer currentLoad = 0;

    @Column(name = "max_groups", nullable = false)
    private Integer maxGroups = 3;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}