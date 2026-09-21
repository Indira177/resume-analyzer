package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.JobMatch;
import com.resumeanalyzer.repository.JobMatchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResumeImprovementService {

    private final JobMatchRepository jobMatchRepository;

    public ResumeImprovementService(
            JobMatchRepository jobMatchRepository) {

        this.jobMatchRepository = jobMatchRepository;
    }

    // =========================================================
    // GET RESUME IMPROVEMENT
    // =========================================================

    public Map<String, Object> getResumeImprovement(
            Long resumeId,
            Long jobDescriptionId) {

        // Find existing job match
        JobMatch jobMatch =
                jobMatchRepository
                        .findByResumeIdAndJobDescriptionId(
                                resumeId,
                                jobDescriptionId);

        if (jobMatch == null) {

            throw new RuntimeException(
                    "Job match not found. Please generate the job match first.");
        }

        // =====================================================
        // GET MISSING SKILLS
        // =====================================================

        List<String> missingSkills =
                new ArrayList<>();

        String missingSkillsText =
                jobMatch.getMissingSkills();

        if (missingSkillsText != null &&
                !missingSkillsText.trim().isEmpty()) {

            String[] skills =
                    missingSkillsText.split(",");

            for (String skill : skills) {

                String cleanedSkill =
                        skill.trim();

                if (!cleanedSkill.isEmpty()) {
                    missingSkills.add(cleanedSkill);
                }
            }
        }

        // =====================================================
        // CREATE IMPROVEMENT RECOMMENDATIONS
        // =====================================================

        List<String> improvements =
                new ArrayList<>();

        for (String skill : missingSkills) {

            switch (skill.toLowerCase()) {

                case "docker":

                    improvements.add(
                            "Learn Docker containerization, " +
                                    "Docker images, containers and Docker Compose.");

                    break;

                case "kubernetes":

                    improvements.add(
                            "Learn Kubernetes basics, pods, " +
                                    "deployments, services and scaling.");

                    break;

                case "aws":

                    improvements.add(
                            "Learn AWS cloud services such as " +
                                    "EC2, S3, RDS and IAM.");

                    break;

                case "azure":

                    improvements.add(
                            "Learn Microsoft Azure fundamentals, " +
                                    "virtual machines, storage and databases.");

                    break;

                case "hibernate":

                    improvements.add(
                            "Improve Hibernate and JPA knowledge, " +
                                    "including entity mapping and relationships.");

                    break;

                case "git":

                    improvements.add(
                            "Practice Git commands, branching, " +
                                    "merging and pull requests.");

                    break;

                case "maven":

                    improvements.add(
                            "Learn Maven dependency management, " +
                                    "build lifecycle and project configuration.");

                    break;

                case "javascript":

                    improvements.add(
                            "Improve JavaScript fundamentals, " +
                                    "ES6, asynchronous programming and DOM.");

                    break;

                case "html":

                    improvements.add(
                            "Improve HTML5 semantic elements, " +
                                    "forms and accessibility.");

                    break;

                case "css":

                    improvements.add(
                            "Improve CSS layouts, Flexbox, Grid " +
                                    "and responsive design.");

                    break;

                default:

                    improvements.add(
                            "Improve your knowledge and practical " +
                                    "experience in " + skill + ".");
            }
        }

        // =====================================================
        // CREATE RESPONSE
        // =====================================================

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "resumeId",
                resumeId);

        response.put(
                "missingSkills",
                missingSkills);

        response.put(
                "jobDescriptionId",
                jobDescriptionId);

        response.put(
                "currentMatchScore",
                jobMatch.getMatchScore());

        response.put(
                "improvements",
                improvements);

        return response;
    }
}

