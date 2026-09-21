package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.entity.JobMatch;
import com.resumeanalyzer.repository.JobDescriptionRepository;
import com.resumeanalyzer.repository.JobMatchRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final JobMatchRepository jobMatchRepository;
    private final JobDescriptionRepository jobDescriptionRepository;

    public DashboardService(
            JobMatchRepository jobMatchRepository,
            JobDescriptionRepository jobDescriptionRepository) {

        this.jobMatchRepository = jobMatchRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    public Map<String, Object> getDashboard(Long resumeId) {

        // Get all matches
        List<JobMatch> matches =
                jobMatchRepository.findByResumeId(resumeId);

        if (matches == null || matches.isEmpty()) {
            throw new RuntimeException(
                    "No job matches found for resumeId="
                            + resumeId);
        }

        // Total jobs
        int totalJobsAnalyzed = matches.size();

        // Find best match
        JobMatch bestMatch = Collections.max(
                matches,
                Comparator.comparing(
                        JobMatch::getMatchScore,
                        Comparator.nullsFirst(
                                Comparator.naturalOrder()
                        )
                )
        );

        int bestMatchScore = bestMatch.getMatchScore();

        // Calculate average score
        int totalScore = 0;
        int scoreCount = 0;

        for (JobMatch match : matches) {

            if (match.getMatchScore() != null) {
                totalScore += match.getMatchScore();
                scoreCount++;
            }
        }

        int averageMatchScore = 0;

        if (scoreCount > 0) {
            averageMatchScore =
                    totalScore / scoreCount;
        }

        // Get best job description
        JobDescription bestJob =
                jobDescriptionRepository
                        .findById(
                                bestMatch.getJobDescriptionId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Job description not found"));

        // Collect missing skills
        Set<String> missingSkillsSet =
                new LinkedHashSet<>();

        for (JobMatch match : matches) {

            String missingSkills =
                    match.getMissingSkills();

            if (missingSkills != null &&
                    !missingSkills.trim().isEmpty()) {

                String[] skills =
                        missingSkills.split(",");

                for (String skill : skills) {

                    String cleanedSkill =
                            skill.trim().toLowerCase();

                    if (!cleanedSkill.isEmpty()) {
                        missingSkillsSet.add(
                                cleanedSkill);
                    }
                }
            }
        }

        // Best job response
        Map<String, Object> bestJobResponse =
                new LinkedHashMap<>();

        bestJobResponse.put(
                "id",
                bestJob.getId());

        bestJobResponse.put(
                "title",
                bestJob.getTitle());

        bestJobResponse.put(
                "matchScore",
                bestMatchScore);

        // Final dashboard response
        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "resumeId",
                resumeId);

        response.put(
                "totalJobsAnalyzed",
                totalJobsAnalyzed);

        response.put(
                "bestMatchScore",
                bestMatchScore);

        response.put(
                "averageMatchScore",
                averageMatchScore);

        response.put(
                "bestJob",
                bestJobResponse);

        response.put(
                "commonMissingSkills",
                new ArrayList<>(
                        missingSkillsSet));

        return response;
    }
}

