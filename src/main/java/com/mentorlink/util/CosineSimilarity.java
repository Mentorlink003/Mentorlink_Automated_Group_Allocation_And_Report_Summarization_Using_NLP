package com.mentorlink.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cosine similarity for skill-based student grouping and faculty-student matching.
 */
public final class CosineSimilarity {

    private CosineSimilarity() {}

    /**
     * Compute cosine similarity between two skill vectors (0.0 to 1.0).
     */
    public static double similarity(Set<String> skills1, Set<String> skills2) {
        if (skills1 == null || skills2 == null || skills1.isEmpty() || skills2.isEmpty()) {
            return 0.0;
        }
        Set<String> allTerms = new HashSet<>(skills1);
        allTerms.addAll(skills2);

        double[] v1 = toVector(skills1, allTerms);
        double[] v2 = toVector(skills2, allTerms);

        double dot = 0, norm1 = 0, norm2 = 0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        double denom = Math.sqrt(norm1) * Math.sqrt(norm2);
        return denom == 0 ? 0 : dot / denom;
    }

    private static double[] toVector(Set<String> skills, Set<String> vocabulary) {
        List<String> sorted = new ArrayList<>(vocabulary);
        Collections.sort(sorted);
        double[] v = new double[sorted.size()];
        int i = 0;
        for (String term : sorted) {
            v[i++] = skills.contains(term) ? 1.0 : 0.0;
        }
        return v;
    }

    /**
     * Group students by skill similarity using greedy clustering.
     * Target group size: 2–3.
     */
    public static List<Set<Long>> clusterBySimilarity(
            Map<Long, Set<String>> studentSkills,
            int minGroupSize,
            int maxGroupSize
    ) {
        List<Long> students = new ArrayList<>(studentSkills.keySet());
        if (students.isEmpty()) return List.of();

        Set<Long> assigned = new HashSet<>();
        List<Set<Long>> groups = new ArrayList<>();

        for (Long seed : students) {
            if (assigned.contains(seed)) continue;

            Set<Long> group = new HashSet<>();
            group.add(seed);
            assigned.add(seed);

            // Greedily add most similar unassigned students
            while (group.size() < maxGroupSize) {
                Long best = null;
                double bestSim = -1;

                for (Long other : students) {
                    if (assigned.contains(other)) continue;
                    double sim = avgSimilarityToGroup(other, group, studentSkills);
                    if (sim > bestSim) {
                        bestSim = sim;
                        best = other;
                    }
                }
                if (best == null || bestSim < 0.01) break;
                group.add(best);
                assigned.add(best);
            }

            if (group.size() >= minGroupSize) {
                groups.add(group);
            }
        }

        // Handle remaining unassigned (singletons or small groups)
        for (Long s : students) {
            if (!assigned.contains(s)) {
                groups.add(Set.of(s));
            }
        }

        return groups;
    }

    private static double avgSimilarityToGroup(Long student, Set<Long> group, Map<Long, Set<String>> skills) {
        Set<String> sSkills = skills.getOrDefault(student, Set.of());
        double sum = 0;
        int count = 0;
        for (Long g : group) {
            sum += similarity(sSkills, skills.getOrDefault(g, Set.of()));
            count++;
        }
        return count == 0 ? 0 : sum / count;
    }
}
