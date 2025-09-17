package com.mentorlink.modules.projects.entity;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String title;

    @Column(length = 2000)
    private String description;

    private String domain;

    private String techStack;

    private String status;

    @Column(nullable = false)
    private Integer progress = 0;

    // optional mentor FK (faculty_profiles)
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private FacultyProfile mentor;

    // optional one-to-one project_group (if you use groups table)
    @OneToOne
    @JoinColumn(name = "group_id", unique = true)
    private Group group;

    // join token for students to join: short code
    @Column(name = "join_token", unique = true)
    private String joinToken;

    // students participating in project
    @ManyToMany
    @JoinTable(
            name = "project_students",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private Set<User> students = new HashSet<>();
}
