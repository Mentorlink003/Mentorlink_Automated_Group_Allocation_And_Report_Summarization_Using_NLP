package com.mentorlink.modules.groups.entity;

import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.users.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project_groups")  // ✅ changed from "groups"
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "join_token", unique = true, nullable = false)
    private String joinToken;

    // Optional token used for faculty to join as mentor
    @Column(name = "mentor_join_token", unique = true)
    private String mentorJoinToken;

    // ✅ Leader of the group (a student user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    @JsonIgnore   // 🔑 prevents infinite recursion
    private User leader;

    // ✅ One group is linked to one project
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // ✅ Members of the group
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    @JsonIgnore   // 🔑 prevents recursion
    private Set<User> members = new HashSet<>();
}
