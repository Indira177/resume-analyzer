package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.AnalysisResult;
import com.resumeanalyzer.service.ResumeAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(
            ResumeAnalysisService resumeAnalysisService) {

        this.resumeAnalysisService = resumeAnalysisService;
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<AnalysisResult> analyzeResume(
            @PathVariable Long id) {

        try {

            AnalysisResult result =
                    resumeAnalysisService.analyzeResume(id);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().build();

        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
    }
}