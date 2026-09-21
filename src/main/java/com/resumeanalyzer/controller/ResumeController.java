package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.entity.User;
import com.resumeanalyzer.repository.UserRepository;
import com.resumeanalyzer.service.ResumeService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;

    public ResumeController(
            ResumeService resumeService,
            UserRepository userRepository) {

        this.resumeService = resumeService;
        this.userRepository = userRepository;
    }

    // =====================================================
    // GET LOGGED-IN USER ID
    // =====================================================

    private Long getLoggedInUserId(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            throw new IllegalArgumentException("User is not logged in");
        }

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return user.getId();
    }

    // =====================================================
    // UPLOAD RESUME
    // =====================================================

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {

            Long userId = getLoggedInUserId(authentication);

            Resume savedResume =
                    resumeService.uploadResume(file, userId);

            return ResponseEntity.ok(savedResume);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (IOException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to read PDF file"
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Resume upload failed"
                    ));
        }
    }

    // =====================================================
    // GET MY RESUME HISTORY
    // =====================================================

    @GetMapping("/my-resumes")
    public ResponseEntity<?> getMyResumes(
            Authentication authentication) {

        try {

            Long userId = getLoggedInUserId(authentication);

            List<Resume> resumes =
                    resumeService.getMyResumes(userId);

            return ResponseEntity.ok(resumes);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to load resume history"
                    ));
        }
    }

    // =====================================================
    // VIEW ONE RESUME
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getMyResume(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            Long userId = getLoggedInUserId(authentication);

            return resumeService
                    .getMyResume(id, userId)
                    .map(resume -> {

                        Map<String, Object> response =
                                new HashMap<>();

                        response.put("success", true);
                        response.put("id", resume.getId());
                        response.put("fileName", resume.getFileName());
                        response.put("fileType", resume.getFileType());
                        response.put("uploadedDate", resume.getUploadedAt());
                        response.put("uploadedAt", resume.getUploadedAt());
                        response.put("resumeText", resume.getResumeText());

                        return ResponseEntity.ok(response);

                    })
                    .orElseGet(() ->

                            ResponseEntity
                                    .status(HttpStatus.NOT_FOUND)
                                    .body(Map.of(
                                            "success", false,
                                            "message", "Resume not found"
                                    ))
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to view resume"
                    ));
        }
    }

    // =====================================================
    // DELETE MY RESUME
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMyResume(
            @PathVariable Long id,
            Authentication authentication) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            Long userId = getLoggedInUserId(authentication);

            boolean deleted =
                    resumeService.deleteMyResume(id, userId);

            if (!deleted) {

                response.put("success", false);
                response.put("message", "Resume not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            response.put("success", true);
            response.put(
                    "message",
                    "Resume deleted successfully"
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put(
                    "message",
                    "Unable to delete resume"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // =====================================================
    // DOWNLOAD RESUME
    // =====================================================

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadResume(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            // Get the actual logged-in user's ID
            Long userId = getLoggedInUserId(authentication);

            // Find only this user's resume
            Resume resume =
                    resumeService
                            .getMyResume(id, userId)
                            .orElse(null);

            if (resume == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Resume not found"
                        ));
            }

            // Generate PDF from the stored resume text
            byte[] pdfBytes =
                    resumeService.generateResumePdf(resume);

            ByteArrayResource resource =
                    new ByteArrayResource(pdfBytes);

            String fileName =
                    resume.getFileName();

            if (fileName == null ||
                    fileName.trim().isEmpty()) {

                fileName = "resume.pdf";
            }

            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName = fileName + ".pdf";
            }

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    fileName + "\""
                    )
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Unable to download resume"
                    ));
        }
    }
}