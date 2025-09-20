package com.mentorlink.modules.users.entity;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.students.entity.StudentProfile;
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

    // ✅ Roles
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    // ✅ Skills
    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    @Builder.Default
    private Set<String> skills = new HashSet<>();

    // ✅ Achievements
    @ElementCollection
    @CollectionTable(name = "user_achievements", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "achievement")
    @Builder.Default
    private Set<String> achievements = new HashSet<>();

    // ✅ Student Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StudentProfile studentProfile;

    // ✅ Faculty Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FacultyProfile facultyProfile;
}
