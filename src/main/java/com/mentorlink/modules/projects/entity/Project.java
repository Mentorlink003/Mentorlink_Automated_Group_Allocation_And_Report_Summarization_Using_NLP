package com.mentorlink.modules.projects.entity;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

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
    @OneToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private String title;
    private String description;
    private String domain;
    private String techStack;
    private String status;
    private int progress;

    @Column(unique = true, nullable = false)
    private String joinToken;

    // ✅ Students in project
    @ManyToMany
    @JoinTable(
            name = "project_students",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private Set<User> students = new HashSet<>();

    // ✅ Mentor (faculty profile, not user directly)
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private FacultyProfile mentor;
}
