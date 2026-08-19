package com.finsight.backend.controller;

import com.finsight.backend.dto.CreateRecurringTransactionRequest;
import com.finsight.backend.dto.RecurringTransactionResponse;
import com.finsight.backend.dto.UpdateRecurringTransactionRequest;
import com.finsight.backend.service.RecurringTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {

    private final RecurringTransactionService
            recurringTransactionService;

    public RecurringTransactionController(
            RecurringTransactionService recurringTransactionService) {

        this.recurringTransactionService =
                recurringTransactionService;
    }

    @PostMapping
    public ResponseEntity<RecurringTransactionResponse> create(
            @Valid @RequestBody
            CreateRecurringTransactionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        recurringTransactionService.create(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<RecurringTransactionResponse>>
    getAll() {

        return ResponseEntity.ok(
                recurringTransactionService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponse>
    getById(@PathVariable String id) {

        return ResponseEntity.ok(
                recurringTransactionService.getById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {

        recurringTransactionService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponse> update(
            @PathVariable String id,
            @RequestBody UpdateRecurringTransactionRequest request) {

        return ResponseEntity.ok(
                recurringTransactionService.update(id, request)
        );
    }
}