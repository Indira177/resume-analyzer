package com.resumeanalyzer.controller;

import com.resumeanalyzer.service.JobRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job-recommendations")
public class JobRecommendationController {

    private final JobRecommendationService jobRecommendationService;

    public JobRecommendationController(
            JobRecommendationService jobRecommendationService) {

        this.jobRecommendationService =
                jobRecommendationService;
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<List<Map<String, Object>>>
    getRecommendedJobs(
            @PathVariable Long resumeId) {

        List<Map<String, Object>> recommendations =
                jobRecommendationService
                        .getRecommendedJobs(resumeId);

        return ResponseEntity.ok(
                recommendations);
    }
}
