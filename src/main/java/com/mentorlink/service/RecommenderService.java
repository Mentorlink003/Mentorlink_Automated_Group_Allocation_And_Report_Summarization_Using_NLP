package com.mentorlink.service;

import com.mentorlink.modules.admin.dto.AutoGroupResultDto;
import com.mentorlink.modules.deadlines.entity.DeadlineType;
import com.mentorlink.modules.deadlines.service.DeadlineService;
import com.mentorlink.modules.faculty.entity.FacultyProfile;
import com.mentorlink.modules.faculty.repository.FacultyProfileRepository;
import com.mentorlink.modules.groups.dto.GroupResponseDto;
import com.mentorlink.modules.groups.entity.Group;
import com.mentorlink.modules.groups.repository.GroupRepository;
import com.mentorlink.modules.groups.service.GroupService;
import com.mentorlink.modules.projects.entity.Project;
import com.mentorlink.modules.projects.repository.ProjectRepository;
import com.mentorlink.modules.students.entity.StudentProfile;
import com.mentorlink.modules.students.repository.StudentProfileRepository;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.util.CosineSimilarity;
import com.mentorlink.util.ExcelProcessor;
import com.mentorlink.util.LeftoverStudentRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommenderService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DeadlineService deadlineService;
    private final ExcelProcessor excelProcessor;
    private final GroupService groupService;

    private static final int MAX_GROUPS_PER_FACULTY = 3;

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

    /**
     * Auto-group leftover students from Excel using cosine similarity on skills, department, year.
     * Run after GROUP_FORMATION deadline. Admin uploads Excel with leftover student emails/roll numbers.
     */
    @Transactional
    public AutoGroupResultDto autoGroupFromExcel(MultipartFile file) {
        if (!deadlineService.isPastDeadline(DeadlineType.GROUP_FORMATION)) {
            throw new IllegalStateException("Group formation deadline has not passed. Auto-allocation is only available after the manual group formation deadline.");
        }

        List<String> studentsNotFound = new ArrayList<>();
        List<String> studentsSkipped = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<GroupResponseDto> createdGroups = new ArrayList<>();

        List<User> toGroup = new ArrayList<>();
        try {
            List<LeftoverStudentRow> rows = excelProcessor.parseLeftoverStudents(file);
            for (LeftoverStudentRow row : rows) {
                User student = resolveStudent(row);
                if (student == null) {
                    String ident = row.getEmail() != null ? row.getEmail() : row.getRollNumber();
                    studentsNotFound.add(ident);
                    continue;
                }
                if (!student.getRoles().contains("STUDENT")) {
                    studentsSkipped.add(student.getEmail() + " (not a student)");
                    continue;
                }
                if (!groupRepository.findByMembersContaining(student).isEmpty()) {
                    studentsSkipped.add(student.getEmail() + " (already in a group)");
                    continue;
                }
                toGroup.add(student);
            }
        } catch (Exception e) {
            errors.add("Excel parse error: " + e.getMessage());
            return AutoGroupResultDto.builder()
                    .groupsCreated(0)
                    .studentsGrouped(0)
                    .facultyAssigned(0)
                    .studentsNotFound(studentsNotFound)
                    .studentsSkipped(studentsSkipped)
                    .errors(errors)
                    .createdGroups(List.of())
                    .build();
        }

        if (toGroup.size() < 2) {
            errors.add("Need at least 2 leftover students to form groups. Found: " + toGroup.size());
            return AutoGroupResultDto.builder()
                    .groupsCreated(0)
                    .studentsGrouped(0)
                    .facultyAssigned(0)
                    .studentsNotFound(studentsNotFound)
                    .studentsSkipped(studentsSkipped)
                    .errors(errors)
                    .createdGroups(List.of())
                    .build();
        }

        // Build skill profile: skills + department + year (for cosine similarity)
        Map<Long, Set<String>> studentSkills = new HashMap<>();
        for (User u : toGroup) {
            Set<String> profile = new HashSet<>();
            if (u.getSkills() != null) profile.addAll(u.getSkills());
            studentProfileRepository.findByUser_Id(u.getId()).ifPresent(sp -> {
                if (sp.getDepartment() != null) profile.add("dept:" + sp.getDepartment().toLowerCase());
                if (sp.getYearOfStudy() != null) profile.add("year:" + sp.getYearOfStudy());
                if (sp.getRollNumber() != null) profile.add("roll:" + sp.getRollNumber().toLowerCase());
            });
            studentSkills.put(u.getId(), profile);
        }

        List<Set<Long>> clusters = CosineSimilarity.clusterBySimilarity(studentSkills, 2, 3);
        int studentsGrouped = 0;
        int facultyAssigned = 0;

        // Faculty with remaining slots (max 3 groups per faculty). Once slots filled, faculty is closed.
        List<FacultyProfile> availableFaculty = new ArrayList<>(facultyProfileRepository.findAll());

        for (Set<Long> cluster : clusters) {
            if (cluster.size() < 2) continue;
            try {
                List<User> members = cluster.stream()
                        .map(id -> userRepository.findById(id).orElseThrow())
                        .toList();
                User leader = members.get(0);

                Project project = Project.builder()
                        .title("Auto-Group Project " + System.currentTimeMillis())
                        .description("Auto-assigned group (cosine similarity)")
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
                        .mentorJoinToken(UUID.randomUUID().toString())
                        .build();
                group.getMembers().addAll(members);
                project.setGroup(group);
                group.setProject(project);
                Group saved = groupRepository.save(group);

                // Auto-assign faculty with remaining slots (only faculty with currentLoad < maxGroups)
                FacultyProfile best = findBestFaculty(availableFaculty, extractProjectSkills(project), project.getDomain());
                if (best != null) {
                    project.setMentor(best);
                    best.setCurrentLoad(best.getCurrentLoad() + 1);
                    projectRepository.save(project);
                    facultyProfileRepository.save(best);
                    facultyAssigned++;
                }

                createdGroups.add(groupService.getById(saved.getId()));
                studentsGrouped += members.size();
            } catch (Exception e) {
                errors.add("Group creation failed: " + e.getMessage());
            }
        }

        return AutoGroupResultDto.builder()
                .groupsCreated(createdGroups.size())
                .studentsGrouped(studentsGrouped)
                .facultyAssigned(facultyAssigned)
                .studentsNotFound(studentsNotFound)
                .studentsSkipped(studentsSkipped)
                .errors(errors.isEmpty() ? List.of() : errors)
                .createdGroups(createdGroups)
                .build();
    }

    private User resolveStudent(LeftoverStudentRow row) {
        if (row.getEmail() != null && !row.getEmail().isBlank()) {
            return userRepository.findByEmail(row.getEmail()).orElse(null);
        }
        if (row.getRollNumber() != null && !row.getRollNumber().isBlank()) {
            return studentProfileRepository.findByRollNumber(row.getRollNumber())
                    .map(StudentProfile::getUser)
                    .orElse(null);
        }
        return null;
    }
}
