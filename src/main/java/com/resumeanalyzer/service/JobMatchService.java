package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.entity.JobMatch;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.repository.JobDescriptionRepository;
import com.resumeanalyzer.repository.JobMatchRepository;
import com.resumeanalyzer.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class JobMatchService {

    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobMatchRepository jobMatchRepository;

    public JobMatchService(
            ResumeRepository resumeRepository,
            JobDescriptionRepository jobDescriptionRepository,
            JobMatchRepository jobMatchRepository) {

        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.jobMatchRepository = jobMatchRepository;
    }

    // =========================================================
    // 1. CREATE OR UPDATE JOB MATCH
    // =========================================================

    public JobMatch matchJob(
            Long resumeId,
            Long jobDescriptionId) {

        // Find resume
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        // Find job description
        JobDescription jobDescription =
                jobDescriptionRepository.findById(jobDescriptionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Job description not found"));

        // Validate resume text
        String resumeText = resume.getResumeText();

        if (resumeText == null ||
                resumeText.trim().isEmpty()) {

            throw new RuntimeException(
                    "Resume does not contain readable text");
        }

        // Get job description
        String jobText = jobDescription.getDescription();

        if (jobText == null ||
                jobText.trim().isEmpty()) {

            throw new RuntimeException(
                    "Job description is empty");
        }

        resumeText = resumeText.toLowerCase();
        jobText = jobText.toLowerCase();

        // =====================================================
        // SUPPORTED SKILLS
        // =====================================================

        List<String> skills = Arrays.asList(
                "java",
                "spring boot",
                "spring",
                "microservices",
                "rest api",
                "mysql",
                "sql",
                "hibernate",
                "docker",
                "kubernetes",
                "aws",
                "azure",
                "git",
                "maven",
                "javascript",
                "html",
                "css"
        );

        // =====================================================
        // COMPARE SKILLS
        // =====================================================

        List<String> matchingSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : skills) {

            boolean requiredByJob =
                    jobText.contains(skill);

            boolean presentInResume =
                    resumeText.contains(skill);

            if (requiredByJob) {

                if (presentInResume) {
                    matchingSkills.add(skill);
                } else {
                    missingSkills.add(skill);
                }
            }
        }

        // =====================================================
        // CALCULATE MATCH SCORE
        // =====================================================

        int totalRequired =
                matchingSkills.size()
                        + missingSkills.size();

        int matchScore = 0;

        if (totalRequired > 0) {

            matchScore =
                    (matchingSkills.size() * 100)
                            / totalRequired;
        }

        // =====================================================
        // RECOMMENDATION
        // =====================================================

        String recommendation;

        if (totalRequired == 0) {

            recommendation =
                    "No supported technical skills were detected " +
                            "in the job description.";

        } else if (missingSkills.isEmpty()) {

            recommendation =
                    "Excellent match. Resume contains the " +
                            "major skills required by the job.";

        } else {

            recommendation =
                    "Consider improving these skills: "
                            + String.join(
                            ", ",
                            missingSkills);
        }

        // =====================================================
        // FIND EXISTING MATCH
        // =====================================================

        JobMatch jobMatch =
                jobMatchRepository
                        .findByResumeIdAndJobDescriptionId(
                                resumeId,
                                jobDescriptionId);

        // =====================================================
        // CREATE IF NOT FOUND
        // =====================================================

        if (jobMatch == null) {

            jobMatch = new JobMatch();

            jobMatch.setResumeId(resumeId);

            jobMatch.setJobDescriptionId(
                    jobDescriptionId);
        }

        // =====================================================
        // UPDATE MATCH RESULT
        // =====================================================

        jobMatch.setMatchScore(matchScore);

        jobMatch.setMatchingSkills(
                String.join(
                        ", ",
                        matchingSkills));

        jobMatch.setMissingSkills(
                String.join(
                        ", ",
                        missingSkills));

        jobMatch.setRecommendation(
                recommendation);

        // Save
        return jobMatchRepository.save(jobMatch);
    }

    // =========================================================
    // 2. GET ONE JOB MATCH
    // =========================================================

    public JobMatch getJobMatch(
            Long resumeId,
            Long jobDescriptionId) {

        JobMatch jobMatch =
                jobMatchRepository
                        .findByResumeIdAndJobDescriptionId(
                                resumeId,
                                jobDescriptionId);

        if (jobMatch == null) {

            throw new RuntimeException(
                    "Job match not found for resumeId="
                            + resumeId
                            + " and jobDescriptionId="
                            + jobDescriptionId);
        }

        return jobMatch;
    }

    // =========================================================
    // 3. GET ALL MATCHES FOR A RESUME
    // =========================================================

    public List<JobMatch> getMatchesByResumeId(
            Long resumeId) {

        return jobMatchRepository
                .findByResumeId(resumeId);
    }

    // =========================================================
    // 4. GET RANKED MATCHES
    // =========================================================

    public List<JobMatch> getRankedMatchesByResumeId(
            Long resumeId) {

        List<JobMatch> matches =
                jobMatchRepository
                        .findByResumeId(resumeId);

        // Highest score first
        matches.sort(
                Comparator.comparing(
                        JobMatch::getMatchScore,
                        Comparator.nullsLast(
                                Comparator.reverseOrder())
                )
        );

        return matches;
    }
}

