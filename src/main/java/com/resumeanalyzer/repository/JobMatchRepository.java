package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobMatchRepository
        extends JpaRepository<JobMatch, Long> {

    // Find one match for a specific resume and job
    JobMatch findByResumeIdAndJobDescriptionId(
            Long resumeId,
            Long jobDescriptionId);

    // Find all matches for a resume
    List<JobMatch> findByResumeId(
            Long resumeId);

    // Find all matches for a resume,
    // highest score first
    List<JobMatch> findByResumeIdOrderByMatchScoreDesc(
            Long resumeId);
}