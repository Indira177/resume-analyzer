package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.service.JobDescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-descriptions")
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    public JobDescriptionController(
            JobDescriptionService jobDescriptionService) {

        this.jobDescriptionService = jobDescriptionService;
    }

    // POST - Create job description
    @PostMapping
    public ResponseEntity<JobDescription> createJobDescription(
            @RequestBody JobDescription jobDescription) {

        JobDescription saved =
                jobDescriptionService.createJobDescription(
                        jobDescription);

        return ResponseEntity.ok(saved);
    }

    // GET - Get job description by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobDescription> getJobDescription(
            @PathVariable Long id) {

        return jobDescriptionService
                .getJobDescription(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Get all job descriptions
    @GetMapping
    public ResponseEntity<List<JobDescription>>
    getAllJobDescriptions() {

        return ResponseEntity.ok(
                jobDescriptionService.getAllJobDescriptions());
    }
}