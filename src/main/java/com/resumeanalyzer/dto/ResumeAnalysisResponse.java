package com.resumeanalyzer.dto;

import java.util.List;

public class ResumeAnalysisResponse {

    private int score;
    private String summary;
    private List<String> skills;
    private List<String> strengths;
    private List<String> suggestions;
    private List<String> missingSections;

    public ResumeAnalysisResponse() {
    }

    public ResumeAnalysisResponse(
            int score,
            String summary,
            List<String> skills,
            List<String> strengths,
            List<String> suggestions,
            List<String> missingSections) {

        this.score = score;
        this.summary = summary;
        this.skills = skills;
        this.strengths = strengths;
        this.suggestions = suggestions;
        this.missingSections = missingSections;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getMissingSections() {
        return missingSections;
    }

    public void setMissingSections(List<String> missingSections) {
        this.missingSections = missingSections;
    }
}