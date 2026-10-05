package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.AnalysisResult;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.repository.AnalysisResultRepository;
import com.resumeanalyzer.repository.ResumeRepository;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public ResumeAnalysisService(
            ResumeRepository resumeRepository,
            AnalysisResultRepository analysisResultRepository) {

        this.resumeRepository = resumeRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    public AnalysisResult analyzeResume(Long resumeId) {

        // Find resume
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Resume not found with id: " + resumeId));

        String text = resume.getResumeText();

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Resume text is empty");
        }

        // Convert text to lowercase for easier keyword checking
        String lowerText = text.toLowerCase();

        // Calculate individual scores
        int skillsScore = calculateSkillsScore(lowerText);

        int experienceScore = calculateExperienceScore(lowerText);

        int educationScore = calculateEducationScore(lowerText);

        int formatScore = calculateFormatScore(text);

        int keywordScore = calculateKeywordScore(lowerText);

        // Overall score
        int overallScore =
                (skillsScore
                        + experienceScore
                        + educationScore
                        + formatScore
                        + keywordScore) / 5;

        // Create result
        AnalysisResult result = new AnalysisResult();

        result.setResumeId(resumeId);

        result.setOverallScore(overallScore);

        result.setSkillsScore(skillsScore);

        result.setExperienceScore(experienceScore);

        result.setEducationScore(educationScore);

        result.setFormatScore(formatScore);

        result.setKeywordScore(keywordScore);

        result.setStrengths(generateStrengths(lowerText));

        result.setWeaknesses(generateWeaknesses(lowerText));

        result.setRecommendations(
                generateRecommendations(lowerText));

        return analysisResultRepository.save(result);
    }

    private int calculateSkillsScore(String text) {

        String[] skills = {
                "java",
                "spring",
                "spring boot",
                "hibernate",
                "mysql",
                "sql",
                "microservices",
                "rest api",
                "git",
                "maven"
        };

        int found = 0;

        for (String skill : skills) {

            if (text.contains(skill)) {
                found++;
            }
        }

        return Math.min(found * 10, 100);
    }

    private int calculateExperienceScore(String text) {

        int score = 0;

        if (text.contains("experience")) {
            score += 30;
        }

        if (text.contains("developer")) {
            score += 20;
        }

        if (text.contains("software engineer")) {
            score += 20;
        }

        if (text.contains("project")) {
            score += 15;
        }

        if (text.contains("responsibilities")) {
            score += 15;
        }

        return Math.min(score, 100);
    }

    private int calculateEducationScore(String text) {

        int score = 0;

        if (text.contains("master")) {
            score += 40;
        }

        if (text.contains("mca")) {
            score += 30;
        }

        if (text.contains("graduated")) {
            score += 15;
        }

        if (text.contains("education")) {
            score += 15;
        }

        return Math.min(score, 100);
    }

    private int calculateFormatScore(String text) {

        String normalizedText = text
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();

        int score = 0;

        if (normalizedText.contains("professional overview")) {
            score += 20;
        }

        if (normalizedText.contains("project history")) {
            score += 20;
        }

        if (normalizedText.contains("technical skills")) {
            score += 20;
        }

        if (normalizedText.contains("responsibilities")) {
            score += 20;
        }

        if (normalizedText.contains("scholastics")) {
            score += 20;
        }

        return Math.min(score, 100);
    }

    private int calculateKeywordScore(String text) {

        String[] keywords = {
                "java",
                "spring boot",
                "microservices",
                "rest",
                "mysql",
                "git",
                "maven",
                "hibernate",
                "sql",
                "api"
        };

        int found = 0;

        for (String keyword : keywords) {

            if (text.contains(keyword)) {
                found++;
            }
        }

        return Math.min(found * 10, 100);
    }

    private String generateStrengths(String text) {

        StringBuilder strengths = new StringBuilder();

        if (text.contains("java")) {
            strengths.append("Strong Java experience; ");
        }

        if (text.contains("spring boot")) {
            strengths.append("Spring Boot experience; ");
        }

        if (text.contains("microservices")) {
            strengths.append("Microservices knowledge; ");
        }

        if (text.contains("rest")) {
            strengths.append("REST API experience; ");
        }

        if (text.contains("hibernate")) {
            strengths.append("Hibernate experience; ");
        }

        if (text.contains("mysql")) {
            strengths.append("Database experience with MySQL; ");
        }

        if (text.contains("git")) {
            strengths.append("Version control experience; ");
        }

        if (strengths.length() == 0) {
            strengths.append(
                    "Resume contains professional information.");
        }

        return strengths.toString();
    }

    private String generateWeaknesses(String text) {

        StringBuilder weaknesses = new StringBuilder();

        if (!text.contains("docker")) {
            weaknesses.append(
                    "Docker experience not mentioned; ");
        }

        if (!text.contains("kubernetes")) {
            weaknesses.append(
                    "Kubernetes experience not mentioned; ");
        }

        if (!text.contains("aws")) {
            weaknesses.append(
                    "AWS/cloud experience not mentioned; ");
        }

        if (!text.contains("azure")) {
            weaknesses.append(
                    "Azure experience not mentioned; ");
        }

        if (!text.contains("achievements")) {
            weaknesses.append(
                    "Quantifiable achievements are not clearly mentioned; ");
        }

        return weaknesses.toString();
    }

    private String generateRecommendations(String text) {

        StringBuilder recommendations = new StringBuilder();

        if (!text.contains("docker")) {
            recommendations.append(
                    "Consider adding Docker experience if applicable; ");
        }

        if (!text.contains("kubernetes")) {
            recommendations.append(
                    "Consider adding Kubernetes experience if applicable; ");
        }

        if (!text.contains("aws")) {
            recommendations.append(
                    "Consider mentioning AWS or cloud experience if applicable; ");
        }

        if (!text.contains("achievement")) {
            recommendations.append(
                    "Add measurable achievements such as performance improvements, "
                            + "cost savings, or number of users supported; ");
        }

        recommendations.append(
                "Keep the resume concise and use clear achievement-oriented bullet points.");

        return recommendations.toString();
    }

    // Get saved analysis for a resume
    public AnalysisResult getAnalysisByResumeId(Long resumeId) {

        return analysisResultRepository
                .findByResumeId(resumeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Analysis not found for resume id: "
                                        + resumeId));
    }
}