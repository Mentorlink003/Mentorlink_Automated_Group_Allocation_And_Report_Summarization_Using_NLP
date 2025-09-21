package com.mentorlink.modules.projects.entity;

import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.groups.entity.Group;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String domain;
    private String techStack;

    // ✅ Each project belongs to one group
    @OneToOne(mappedBy = "project")
    private Group group;

    // ✅ Mentor (faculty)
    @ManyToOne
    private FacultyProfile mentor;
}
