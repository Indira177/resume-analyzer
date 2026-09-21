package com.resumeanalyzer.controller;

import com.resumeanalyzer.service.ResumeImprovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/resume-improvement")
public class ResumeImprovementController {

    private final ResumeImprovementService
            resumeImprovementService;

    public ResumeImprovementController(
            ResumeImprovementService resumeImprovementService) {

        this.resumeImprovementService =
                resumeImprovementService;
    }

    // =========================================================
    // GET RESUME IMPROVEMENT
    // =========================================================

    @GetMapping("/{resumeId}/{jobDescriptionId}")
    public ResponseEntity<Map<String, Object>>
    getResumeImprovement(
            @PathVariable Long resumeId,
            @PathVariable Long jobDescriptionId) {

        Map<String, Object> result =
                resumeImprovementService
                        .getResumeImprovement(
                                resumeId,
                                jobDescriptionId);

        return ResponseEntity.ok(result);
    }
}

