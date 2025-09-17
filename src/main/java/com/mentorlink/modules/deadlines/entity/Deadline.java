package com.mentorlink.modules.deadlines.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "deadlines")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant dueDate;
}
