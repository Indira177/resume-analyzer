package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.AnalysisResult;
import com.resumeanalyzer.service.ResumeAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin
public class AnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    public AnalysisController(
            ResumeAnalysisService resumeAnalysisService) {

        this.resumeAnalysisService = resumeAnalysisService;
    }

    // Create / calculate resume analysis
    @PostMapping("/{resumeId}")
    public ResponseEntity<?> analyzeResume(
            @PathVariable Long resumeId) {

        try {

            AnalysisResult result =
                    resumeAnalysisService.analyzeResume(resumeId);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to analyze resume"
                    ));
        }
    }

    // Get previously saved resume analysis
    @GetMapping("/{resumeId}")
    public ResponseEntity<?> getAnalysis(
            @PathVariable Long resumeId) {

        try {

            AnalysisResult result =
                    resumeAnalysisService
                            .getAnalysisByResumeId(resumeId);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to get resume analysis"
                    ));
        }
    }
}