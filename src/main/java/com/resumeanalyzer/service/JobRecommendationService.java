package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.entity.JobMatch;
import com.resumeanalyzer.repository.JobDescriptionRepository;
import com.resumeanalyzer.repository.JobMatchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobRecommendationService {

    private final JobMatchRepository jobMatchRepository;
    private final JobDescriptionRepository jobDescriptionRepository;

    public JobRecommendationService(
            JobMatchRepository jobMatchRepository,
            JobDescriptionRepository jobDescriptionRepository) {

        this.jobMatchRepository = jobMatchRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    public List<Map<String, Object>> getRecommendedJobs(
            Long resumeId) {

        // Get all matches for resume
        List<JobMatch> matches =
                jobMatchRepository
                        .findByResumeIdOrderByMatchScoreDesc(
                                resumeId);

        List<Map<String, Object>> recommendations =
                new ArrayList<>();

        for (JobMatch match : matches) {

            // Find job description
            JobDescription jobDescription =
                    jobDescriptionRepository
                            .findById(
                                    match.getJobDescriptionId())
                            .orElse(null);

            Map<String, Object> recommendation =
                    new LinkedHashMap<>();

            recommendation.put(
                    "jobDescriptionId",
                    match.getJobDescriptionId());

            if (jobDescription != null) {

                recommendation.put(
                        "jobTitle",
                        jobDescription.getTitle());

            } else {

                recommendation.put(
                        "jobTitle",
                        "Unknown");
            }

            recommendation.put(
                    "matchScore",
                    match.getMatchScore());

            recommendation.put(
                    "matchingSkills",
                    match.getMatchingSkills());

            recommendation.put(
                    "missingSkills",
                    match.getMissingSkills());

            recommendation.put(
                    "recommendation",
                    match.getRecommendation());

            recommendations.add(
                    recommendation);
        }

        return recommendations;
    }
}

