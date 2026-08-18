package com.finsight.backend.controller;

import com.finsight.backend.dto.CreateTransactionRequest;
import com.finsight.backend.dto.TransactionFilterRequest;
import com.finsight.backend.dto.TransactionResponse;
import com.finsight.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        TransactionResponse response =
                transactionService.createTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @ModelAttribute TransactionFilterRequest filter) {

        return ResponseEntity.ok(
                transactionService.getTransactions(filter)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String id) {

        return ResponseEntity.ok(
                transactionService.getTransaction(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable String id,
            @Valid @RequestBody CreateTransactionRequest request) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable String id) {

        transactionService.deleteTransaction(id);

        return ResponseEntity.noContent().build();
    }
}