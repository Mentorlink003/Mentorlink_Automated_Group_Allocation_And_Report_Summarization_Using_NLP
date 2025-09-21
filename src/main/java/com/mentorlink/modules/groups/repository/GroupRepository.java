package com.mentorlink.modules.groups.repository;

import com.mentorlink.modules.groups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByJoinToken(String joinToken);

    boolean existsByProjectId(Long projectId);
}
