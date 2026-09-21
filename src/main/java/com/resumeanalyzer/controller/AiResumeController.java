package com.resumeanalyzer.controller;

import com.resumeanalyzer.service.AiResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5500")
public class AiResumeController {

    private final AiResumeService aiResumeService;

    public AiResumeController(
            AiResumeService aiResumeService) {

        this.aiResumeService = aiResumeService;
    }

    @PostMapping("/analyze/{resumeId}")
    public ResponseEntity<?> analyzeResumeWithAI(
            @PathVariable Long resumeId) {

        try {

            String aiResult =
                    aiResumeService.analyzeResumeWithAI(resumeId);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "resumeId", resumeId,
                            "aiAnalysis", aiResult
                    )
            );

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
                            "message",
                            "Unable to analyze resume with AI"
                    ));
        }
    }
}