package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.entity.JobMatch;
import com.resumeanalyzer.repository.JobDescriptionRepository;
import com.resumeanalyzer.repository.JobMatchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {

    private final JobMatchRepository jobMatchRepository;
    private final JobDescriptionRepository jobDescriptionRepository;

    public AnalysisService(
            JobMatchRepository jobMatchRepository,
            JobDescriptionRepository jobDescriptionRepository) {

        this.jobMatchRepository = jobMatchRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    // =========================================================
    // GET DASHBOARD SUMMARY
    // =========================================================

    public Map<String, Object> getSummary(Long resumeId) {

        List<JobMatch> matches =
                jobMatchRepository.findByResumeId(resumeId);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("resumeId", resumeId);

        // =====================================================
        // NO MATCHES
        // =====================================================

        if (matches == null || matches.isEmpty()) {

            response.put("totalJobsAnalyzed", 0);
            response.put("bestMatchScore", 0);
            response.put("averageMatchScore", 0);
            response.put("bestJob", null);
            response.put(
                    "commonMissingSkills",
                    new ArrayList<>());

            return response;
        }

        // =====================================================
        // TOTAL JOBS
        // =====================================================

        response.put(
                "totalJobsAnalyzed",
                matches.size());

        // =====================================================
        // BEST AND AVERAGE SCORE
        // =====================================================

        int totalScore = 0;
        int bestScore = 0;

        JobMatch bestMatch = null;

        for (JobMatch match : matches) {

            int score =
                    match.getMatchScore() == null
                            ? 0
                            : match.getMatchScore();

            totalScore += score;

            if (bestMatch == null ||
                    score > bestScore) {

                bestScore = score;
                bestMatch = match;
            }
        }

        int averageScore =
                totalScore / matches.size();

        response.put(
                "bestMatchScore",
                bestScore);

        response.put(
                "averageMatchScore",
                averageScore);

        // =====================================================
        // BEST JOB
        // =====================================================

        if (bestMatch != null) {

            Map<String, Object> bestJob =
                    new LinkedHashMap<>();

            Long jobDescriptionId =
                    bestMatch.getJobDescriptionId();

            bestJob.put(
                    "id",
                    jobDescriptionId);

            JobDescription jobDescription =
                    jobDescriptionRepository
                            .findById(jobDescriptionId)
                            .orElse(null);

            if (jobDescription != null) {

                bestJob.put(
                        "title",
                        jobDescription.getTitle());

            } else {

                bestJob.put(
                        "title",
                        "Unknown Job");
            }

            bestJob.put(
                    "matchScore",
                    bestScore);

            response.put(
                    "bestJob",
                    bestJob);
        }

        // =====================================================
        // COMMON MISSING SKILLS
        // =====================================================

        Map<String, Integer> skillFrequency =
                new HashMap<>();

        for (JobMatch match : matches) {

            String missingSkills =
                    match.getMissingSkills();

            if (missingSkills == null ||
                    missingSkills.trim().isEmpty()) {

                continue;
            }

            String[] skills =
                    missingSkills.split(",");

            for (String skill : skills) {

                String cleanedSkill =
                        skill.trim().toLowerCase();

                if (!cleanedSkill.isEmpty()) {

                    skillFrequency.put(
                            cleanedSkill,
                            skillFrequency.getOrDefault(
                                    cleanedSkill,
                                    0) + 1);
                }
            }
        }

        // =====================================================
        // SORT MISSING SKILLS BY FREQUENCY
        // =====================================================

        List<String> commonMissingSkills =
                new ArrayList<>(
                        skillFrequency.keySet());

        commonMissingSkills.sort(
                (a, b) -> Integer.compare(
                        skillFrequency.get(b),
                        skillFrequency.get(a)));

        response.put(
                "commonMissingSkills",
                commonMissingSkills);

        return response;
    }
}