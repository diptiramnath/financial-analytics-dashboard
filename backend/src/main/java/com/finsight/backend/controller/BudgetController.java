package com.finsight.backend.controller;

import com.finsight.backend.dto.BudgetResponse;
import com.finsight.backend.dto.CreateBudgetRequest;
import com.finsight.backend.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService) {

        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody CreateBudgetRequest request) {

        BudgetResponse response =
                budgetService.createBudget(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                budgetService.getBudgets(month, year)
        );
    }
}