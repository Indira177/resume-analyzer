package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDescriptionRepository
        extends JpaRepository<JobDescription, Long> {

}
