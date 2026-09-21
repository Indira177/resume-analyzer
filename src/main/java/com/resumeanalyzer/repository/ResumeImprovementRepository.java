package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.ResumeImprovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeImprovementRepository
        extends JpaRepository<ResumeImprovement, Long> {

    List<ResumeImprovement> findByResumeId(Long resumeId);

    ResumeImprovement findByResumeIdAndJobDescriptionId(
            Long resumeId,
            Long jobDescriptionId);
}

