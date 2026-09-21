package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.AnalysisResult;
import com.resumeanalyzer.service.ResumeAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:5500")
public class AnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    public AnalysisController(
            ResumeAnalysisService resumeAnalysisService) {

        this.resumeAnalysisService = resumeAnalysisService;
    }

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

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to analyze resume"
                    ));
        }
    }
}