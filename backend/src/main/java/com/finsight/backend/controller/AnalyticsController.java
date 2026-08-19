package com.finsight.backend.controller;

import com.finsight.backend.dto.MonthlyAnalyticsResponse;
import com.finsight.backend.dto.MonthlyComparisonResponse;
import com.finsight.backend.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyAnalyticsResponse>
    getMonthlyAnalytics(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                analyticsService.getMonthlyAnalytics(
                        month,
                        year
                )
        );
    }

    @GetMapping("/monthly/comparison")
    public ResponseEntity<MonthlyComparisonResponse>
    getMonthlyComparison(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                analyticsService.getMonthlyComparison(
                        month,
                        year
                )
        );
    }
}