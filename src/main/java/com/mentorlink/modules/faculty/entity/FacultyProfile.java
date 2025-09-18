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

    @Builder.Default
    private int currentLoad = 0;

    @Builder.Default
    private int maxGroups = 3;

    // ✅ Each faculty profile belongs to one User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
