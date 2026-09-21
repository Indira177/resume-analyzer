package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.repository.JobDescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;

    public JobDescriptionService(
            JobDescriptionRepository jobDescriptionRepository) {

        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    // =========================================================
    // CREATE JOB DESCRIPTION
    // =========================================================

    public JobDescription createJobDescription(
            JobDescription jobDescription) {

        if (jobDescription.getTitle() == null ||
                jobDescription.getTitle().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Job title is required");
        }

        if (jobDescription.getDescription() == null ||
                jobDescription.getDescription().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Job description is required");
        }

        return jobDescriptionRepository.save(jobDescription);
    }

    // =========================================================
    // GET JOB DESCRIPTION BY ID
    // =========================================================

    public Optional<JobDescription> getJobDescription(Long id) {

        return jobDescriptionRepository.findById(id);
    }

    // =========================================================
    // GET ALL JOB DESCRIPTIONS
    // =========================================================

    public List<JobDescription> getAllJobDescriptions() {

        return jobDescriptionRepository.findAll();
    }
}