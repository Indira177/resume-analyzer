package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.JobDescription;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.repository.JobDescriptionRepository;
import com.resumeanalyzer.repository.ResumeRepository;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobMatchService jobMatchService;

    public ResumeService(
            ResumeRepository resumeRepository,
            JobDescriptionRepository jobDescriptionRepository,
            JobMatchService jobMatchService) {

        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.jobMatchService = jobMatchService;
    }

    // =====================================================
    // UPLOAD AND ANALYZE RESUME
    // =====================================================

    public Resume uploadResume(
            MultipartFile file,
            Long userId) throws IOException {

        // 1. Validate user

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required");
        }

        // 2. Validate file

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Resume file cannot be empty");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null ||
                !fileName.toLowerCase().endsWith(".pdf")) {

            throw new IllegalArgumentException(
                    "Only PDF files are supported");
        }

        // 3. Prevent duplicate upload

        if (resumeRepository.existsByUserIdAndFileName(
                userId,
                fileName)) {

            throw new IllegalArgumentException(
                    "This resume has already been uploaded.");
        }

        // 4. Read original PDF bytes

        byte[] fileBytes = file.getBytes();

        // 5. Extract text from PDF

        String resumeText;

        try (PDDocument document =
                     Loader.loadPDF(fileBytes)) {

            PDFTextStripper pdfTextStripper =
                    new PDFTextStripper();

            resumeText =
                    pdfTextStripper.getText(document);
        }

        // 6. Create resume entity

        Resume resume = new Resume();

        resume.setFileName(fileName);

        resume.setFileType(
                file.getContentType());

        resume.setResumeText(resumeText);

        // Save original PDF bytes

        resume.setFileData(fileBytes);

        resume.setUploadedAt(
                LocalDateTime.now());

        // Associate with logged-in user

        resume.setUserId(userId);

        // 7. Save resume

        Resume savedResume =
                resumeRepository.save(resume);

        // 8. Get all job descriptions

        List<JobDescription> jobDescriptions =
                jobDescriptionRepository.findAll();

        // 9. Match resume with all jobs

        for (JobDescription jobDescription :
                jobDescriptions) {

            jobMatchService.matchJob(
                    savedResume.getId(),
                    jobDescription.getId());
        }

        return savedResume;
    }

    // =====================================================
    // GET MY RESUME HISTORY
    // =====================================================

    public List<Resume> getMyResumes(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required");
        }

        return resumeRepository
                .findByUserIdOrderByUploadedAtDesc(userId);
    }

    // =====================================================
    // GET ONE RESUME OF LOGGED-IN USER
    // =====================================================

    public Optional<Resume> getMyResume(
            Long resumeId,
            Long userId) {

        if (resumeId == null || userId == null) {
            throw new IllegalArgumentException(
                    "Resume ID and User ID are required");
        }

        return resumeRepository
                .findByIdAndUserId(
                        resumeId,
                        userId);
    }

    // =====================================================
    // DELETE MY RESUME
    // =====================================================

    public boolean deleteMyResume(
            Long resumeId,
            Long userId) {

        if (resumeId == null || userId == null) {
            throw new IllegalArgumentException(
                    "Resume ID and User ID are required");
        }

        Optional<Resume> resume =
                resumeRepository.findByIdAndUserId(
                        resumeId,
                        userId);

        if (resume.isEmpty()) {
            return false;
        }

        resumeRepository.delete(resume.get());

        return true;
    }

    // =====================================================
    // GENERATE PDF FOR DOWNLOAD
    // =====================================================

    public byte[] generateResumePdf(Resume resume) {

        if (resume == null) {
            throw new IllegalArgumentException(
                    "Resume not found");
        }

        byte[] fileData = resume.getFileData();

        if (fileData == null || fileData.length == 0) {
            throw new IllegalArgumentException(
                    "Original PDF file is not available");
        }

        // Return the original uploaded PDF

        return fileData;
    }
}