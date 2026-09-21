package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.repository.ResumeRepository;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiResumeService {

    private final ResumeRepository resumeRepository;
    private final ChatClient chatClient;

    public AiResumeService(
            ResumeRepository resumeRepository,
            ChatClient.Builder chatClientBuilder) {

        this.resumeRepository = resumeRepository;

        this.chatClient = chatClientBuilder.build();
    }

    public String analyzeResumeWithAI(Long resumeId) {

        // Find the resume from MySQL
        Resume resume = resumeRepository
                .findById(resumeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Resume not found with ID: " + resumeId
                        )
                );

        // Get extracted resume text
        String resumeText = resume.getResumeText();

        if (resumeText == null || resumeText.isBlank()) {

            throw new IllegalArgumentException(
                    "Resume text is empty."
            );
        }

        // Send resume text to local Ollama AI
        String prompt = """
                You are a professional resume reviewer and career advisor.

                Analyze the following resume carefully.

                Provide a professional report using these exact sections:

                OVERALL RESUME SCORE
                Give an overall score from 0 to 100.

                ATS COMPATIBILITY SCORE
                Give an ATS compatibility score from 0 to 100.

                RESUME SUMMARY
                Summarize the candidate's education, experience, and professional background.

                KEY STRENGTHS
                List the strongest qualities found in the resume.

                TECHNICAL SKILLS
                List the technical skills clearly.

                MISSING OR WEAK SKILLS
                Identify skills that may need improvement based only on the resume.

                EXPERIENCE ANALYSIS
                Explain the candidate's experience and achievements.

                EDUCATION ANALYSIS
                Summarize the educational qualifications.

                RECOMMENDED JOB ROLES
                Suggest 3 to 5 suitable job roles based on the candidate's actual skills and experience.
                For each role, briefly explain why it is suitable.
                - Recommend unique job roles only. Do not repeat the same role or slightly different versions of the same role.

                IMPROVEMENT SUGGESTIONS
                Give practical suggestions to improve the resume.

                INTERVIEW PREPARATION
                Suggest important interview topics based on the candidate's skills.

                FINAL RECOMMENDATION
                Give a short professional career recommendation.

                IMPORTANT RULES:
                - Use only information available in the resume.
                - Do not invent experience, skills, companies, or qualifications.
                - Do not use tables.
                - Use clear headings and numbered or bullet points.
                - Keep the report professional and easy to understand.

             Resume text:
             """ + resume.getResumeText();

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}