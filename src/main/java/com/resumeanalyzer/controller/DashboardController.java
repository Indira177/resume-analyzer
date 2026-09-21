package com.resumeanalyzer.controller;

import com.resumeanalyzer.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @PathVariable Long resumeId) {

        Map<String, Object> dashboard =
                dashboardService.getDashboard(resumeId);

        return ResponseEntity.ok(dashboard);
    }
}
