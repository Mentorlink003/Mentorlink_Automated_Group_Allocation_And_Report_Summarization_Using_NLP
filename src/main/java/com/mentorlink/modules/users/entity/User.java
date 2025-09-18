package com.mentorlink.modules.users.entity;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    // student-specific
    private String rollNumber;
    private String department;
    private Integer yearOfStudy;

    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_achievements", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "achievement")
    private Set<String> achievements = new HashSet<>();

    // ✅ Faculty profile (only if faculty)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FacultyProfile facultyProfile;
}
