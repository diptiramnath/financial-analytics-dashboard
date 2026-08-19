package com.finsight.backend.controller;

import com.finsight.backend.dto.InsightResponse;
import com.finsight.backend.service.InsightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(
            InsightService insightService) {

        this.insightService = insightService;
    }

    @GetMapping
    public ResponseEntity<List<InsightResponse>> getInsights(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                insightService.getInsights(
                        month,
                        year
                )
        );
    }
}