package com.resumeanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    private String fileType;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String resumeText;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    private LocalDateTime uploadedAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public Resume() {
    }

    public Resume(
            String fileName,
            String fileType,
            String resumeText,
            byte[] fileData,
            LocalDateTime uploadedAt,
            Long userId) {

        this.fileName = fileName;
        this.fileType = fileType;
        this.resumeText = resumeText;
        this.fileData = fileData;
        this.uploadedAt = uploadedAt;
        this.userId = userId;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}