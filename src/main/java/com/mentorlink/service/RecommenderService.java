package com.mentorlink.service;

import com.mentorlink.modules.deadlines.entity.DeadlineType;
import com.mentorlink.modules.deadlines.service.DeadlineService;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.groups.repository.GroupRepository;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.util.CosineSimilarity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommenderService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final DeadlineService deadlineService;

    /**
     * Auto-group unassigned students by skill similarity (2–3 per group).
     * Run after GROUP_FORMATION deadline.
     */
    @Transactional
    public List<Group> autoGroupStudents() {
        if (!deadlineService.isPastDeadline(DeadlineType.GROUP_FORMATION)) {
            throw new IllegalStateException("Group formation deadline has not passed");
        }

        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains("STUDENT"))
                .toList();

        List<User> unassigned = students.stream()
                .filter(s -> groupRepository.findByMembersContaining(s).isEmpty())
                .toList();

        if (unassigned.isEmpty()) return List.of();

        Map<Long, Set<String>> studentSkills = new HashMap<>();
        for (User u : unassigned) {
            studentSkills.put(u.getId(), u.getSkills() != null ? new HashSet<>(u.getSkills()) : new HashSet<>());
        }

        List<Set<Long>> clusters = CosineSimilarity.clusterBySimilarity(studentSkills, 2, 3);
        List<Group> created = new ArrayList<>();

        for (Set<Long> cluster : clusters) {
            if (cluster.size() < 2) continue; // skip singletons for now, or add to existing
            List<User> members = cluster.stream()
                    .map(id -> userRepository.findById(id).orElseThrow())
                    .toList();
            User leader = members.get(0);

            Project project = Project.builder()
                    .title("Auto-Group Project " + System.currentTimeMillis())
                    .description("Auto-assigned group")
                    .domain("General")
                    .techStack("TBD")
                    .progress(0)
                    .build();
            project = projectRepository.save(project);

            Group group = Group.builder()
                    .name("Group-" + project.getId())
                    .project(project)
                    .leader(leader)
                    .joinToken(UUID.randomUUID().toString())
                    .build();
            group.getMembers().addAll(members);
            project.setGroup(group);
            group.setProject(project);
            created.add(groupRepository.save(group));
        }
        return created;
    }

    /**
     * Auto-assign faculty to projects without mentors, by expertise and skill similarity.
     */
    @Transactional
    public int autoAssignFaculty() {
        List<Project> unassigned = projectRepository.findAll().stream()
                .filter(p -> p.getMentor() == null)
                .toList();
        if (unassigned.isEmpty()) return 0;

        List<FacultyProfile> faculty = facultyProfileRepository.findAll().stream()
                .filter(f -> f.getCurrentLoad() < f.getMaxGroups())
                .toList();

        int assigned = 0;
        for (Project project : unassigned) {
            Set<String> projectSkills = extractProjectSkills(project);
            FacultyProfile best = findBestFaculty(faculty, projectSkills, project.getDomain());
            if (best != null) {
                project.setMentor(best);
                best.setCurrentLoad(best.getCurrentLoad() + 1);
                projectRepository.save(project);
                facultyProfileRepository.save(best);
                assigned++;
            }
        }
        return assigned;
    }

    private Set<String> extractProjectSkills(Project project) {
        Set<String> skills = new HashSet<>();
        if (project.getTechStack() != null) {
            skills.addAll(Arrays.asList(project.getTechStack().split("[,;\\s]+")));
        }
        if (project.getDomain() != null) skills.add(project.getDomain());
        return skills;
    }

    private FacultyProfile findBestFaculty(List<FacultyProfile> faculty, Set<String> projectSkills, String domain) {
        FacultyProfile best = null;
        double bestScore = -1;
        for (FacultyProfile f : faculty) {
            if (f.getCurrentLoad() >= f.getMaxGroups()) continue;
            Set<String> exp = new HashSet<>();
            if (f.getExpertise() != null) exp.addAll(Arrays.asList(f.getExpertise().split("[,;\\s]+")));
            if (f.getDepartment() != null) exp.add(f.getDepartment());
            double sim = CosineSimilarity.similarity(projectSkills, exp);
            if (sim > bestScore) {
                bestScore = sim;
                best = f;
            }
        }
        return best;
    }
}
