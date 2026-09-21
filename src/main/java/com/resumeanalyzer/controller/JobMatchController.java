package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.entity.JobMatch;
import com.resumeanalyzer.repository.JobDescriptionRepository;
import com.resumeanalyzer.service.JobMatchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job-match")
public class JobMatchController {

    private final JobMatchService jobMatchService;
    private final JobDescriptionRepository jobDescriptionRepository;

    public JobMatchController(
            JobMatchService jobMatchService,
            JobDescriptionRepository jobDescriptionRepository) {

        this.jobMatchService = jobMatchService;
        this.jobDescriptionRepository = jobDescriptionRepository;
    }


    // =========================================================
    // 1. CREATE OR UPDATE MATCH
    // =========================================================

    @PostMapping("/{resumeId}/{jobDescriptionId}")
    public ResponseEntity<JobMatch> matchJob(
            @PathVariable Long resumeId,
            @PathVariable Long jobDescriptionId) {

        JobMatch result =
                jobMatchService.matchJob(
                        resumeId,
                        jobDescriptionId);

        return ResponseEntity.ok(result);
    }


    // =========================================================
    // 2. GET ONE SPECIFIC MATCH
    // =========================================================

    @GetMapping("/{resumeId}/{jobDescriptionId}")
    public ResponseEntity<?> getJobMatch(
            @PathVariable Long resumeId,
            @PathVariable Long jobDescriptionId) {

        JobMatch result =
                jobMatchService.getJobMatch(
                        resumeId,
                        jobDescriptionId);

        JobDescription jobDescription =
                jobDescriptionRepository.findById(jobDescriptionId)
                        .orElse(null);

        Map<String, Object> response =
                createResponse(result, jobDescription);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // 3. GET ALL MATCHES FOR A RESUME
    // =========================================================

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<Map<String, Object>>> getMatchesByResumeId(
            @PathVariable Long resumeId) {

        List<JobMatch> matches =
                jobMatchService.getMatchesByResumeId(resumeId);

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (JobMatch match : matches) {

            JobDescription jobDescription =
                    jobDescriptionRepository
                            .findById(match.getJobDescriptionId())
                            .orElse(null);

            response.add(
                    createResponse(
                            match,
                            jobDescription)
            );
        }

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // 4. GET RANKED MATCHES
    // =========================================================

    @GetMapping("/ranking/{resumeId}")
    public ResponseEntity<List<Map<String, Object>>> getRankedMatches(
            @PathVariable Long resumeId) {

        List<JobMatch> matches =
                jobMatchService.getRankedMatchesByResumeId(
                        resumeId);

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (JobMatch match : matches) {

            JobDescription jobDescription =
                    jobDescriptionRepository
                            .findById(match.getJobDescriptionId())
                            .orElse(null);

            response.add(
                    createResponse(
                            match,
                            jobDescription)
            );
        }

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // 5. CREATE API RESPONSE
    // =========================================================

    private Map<String, Object> createResponse(
            JobMatch match,
            JobDescription jobDescription) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "id",
                match.getId());

        response.put(
                "resumeId",
                match.getResumeId());

        response.put(
                "jobDescriptionId",
                match.getJobDescriptionId());

        // Actual job title
        if (jobDescription != null) {

            response.put(
                    "jobTitle",
                    jobDescription.getTitle());

        } else {

            response.put(
                    "jobTitle",
                    "Job Description #"
                            + match.getJobDescriptionId());
        }

        response.put(
                "matchScore",
                match.getMatchScore());

        response.put(
                "matchingSkills",
                match.getMatchingSkills());

        response.put(
                "missingSkills",
                match.getMissingSkills());

        response.put(
                "recommendation",
                match.getRecommendation());

        return response;
    }
}