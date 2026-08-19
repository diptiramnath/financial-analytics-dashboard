package com.finsight.backend.service;

import com.finsight.backend.dto.CreateRecurringTransactionRequest;
import com.finsight.backend.dto.RecurringTransactionResponse;
import com.finsight.backend.entity.RecurringTransaction;
import com.finsight.backend.entity.Transaction;
import com.finsight.backend.entity.User;
import com.finsight.backend.enums.RecurringFrequency;
import com.finsight.backend.enums.TransactionStatus;
import com.finsight.backend.repository.RecurringTransactionRepository;
import com.finsight.backend.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.finsight.backend.dto.UpdateRecurringTransactionRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository
            recurringTransactionRepository;

    private final CurrentUserService currentUserService;

    private final TransactionService transactionService;

    public RecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            CurrentUserService currentUserService,
            TransactionService transactionService) {

        this.recurringTransactionRepository =
                recurringTransactionRepository;

        this.currentUserService =
                currentUserService;

        this.transactionService =
                transactionService;
    }

    public RecurringTransactionResponse create(
            CreateRecurringTransactionRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        RecurringTransaction recurring =
                RecurringTransaction.builder()
                        .userId(currentUser.getId())
                        .accountId(request.getAccountId())
                        .categoryId(request.getCategoryId())
                        .amount(request.getAmount())
                        .type(request.getType())
                        .merchant(request.getMerchant())
                        .description(request.getDescription())
                        .paymentMethod(request.getPaymentMethod())
                        .frequency(request.getFrequency())
                        .nextOccurrence(
                                request.getNextOccurrence())
                        .endDate(request.getEndDate())
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        RecurringTransaction saved =
                recurringTransactionRepository.save(recurring);

        return mapToResponse(saved);
    }

    public List<RecurringTransactionResponse> getAll() {

        User currentUser =
                currentUserService.getCurrentUser();

        return recurringTransactionRepository
                .findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RecurringTransactionResponse getById(
            String id) {

        User currentUser =
                currentUserService.getCurrentUser();

        RecurringTransaction recurring =
                recurringTransactionRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recurring transaction not found"
                                ));

        return mapToResponse(recurring);
    }

    public void delete(String id) {

        User currentUser =
                currentUserService.getCurrentUser();

        RecurringTransaction recurring =
                recurringTransactionRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recurring transaction not found"
                                ));

        recurringTransactionRepository.delete(recurring);
    }

    @Scheduled(fixedRate = 60000)
    public void processRecurringTransactions() {

        LocalDateTime now = LocalDateTime.now();

        List<RecurringTransaction> dueTransactions =
                recurringTransactionRepository
                        .findByActiveTrueAndNextOccurrenceLessThanEqual(
                                now
                        );

        for (RecurringTransaction recurring :
                dueTransactions) {

            LocalDateTime occurrence =
                    recurring.getNextOccurrence();

            while (!occurrence.isAfter(now)) {

                if (recurring.getEndDate() != null
                        && occurrence.isAfter(
                        recurring.getEndDate())) {

                    recurring.setActive(false);
                    break;
                }

                transactionService.createTransactionInternal(
                        recurring.getUserId(),
                        recurring.getAccountId(),
                        recurring.getCategoryId(),
                        recurring.getAmount(),
                        recurring.getType(),
                        recurring.getMerchant(),
                        recurring.getDescription(),
                        occurrence,
                        recurring.getPaymentMethod(),
                        null,
                        true
                );

                occurrence =
                        calculateNextOccurrence(
                                occurrence,
                                recurring.getFrequency()
                        );
            }

            if (recurring.isActive()) {

                if (recurring.getEndDate() != null
                        && occurrence.isAfter(
                        recurring.getEndDate())) {

                    recurring.setActive(false);

                } else {

                    recurring.setNextOccurrence(occurrence);
                }
            }

            recurring.setUpdatedAt(LocalDateTime.now());

            recurringTransactionRepository.save(recurring);
        }
    }

    private LocalDateTime calculateNextOccurrence(
            LocalDateTime current,
            RecurringFrequency frequency) {

        return switch (frequency) {

            case DAILY ->
                    current.plusDays(1);

            case WEEKLY ->
                    current.plusWeeks(1);

            case MONTHLY ->
                    current.plusMonths(1);

            case YEARLY ->
                    current.plusYears(1);
        };
    }

    public RecurringTransactionResponse update(
            String id,
            UpdateRecurringTransactionRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        RecurringTransaction recurring =
                recurringTransactionRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recurring transaction not found"
                                ));

        if (request.getAccountId() != null)
            recurring.setAccountId(request.getAccountId());

        if (request.getCategoryId() != null)
            recurring.setCategoryId(request.getCategoryId());

        if (request.getAmount() != null)
            recurring.setAmount(request.getAmount());

        if (request.getType() != null)
            recurring.setType(request.getType());

        if (request.getMerchant() != null)
            recurring.setMerchant(request.getMerchant());

        if (request.getDescription() != null)
            recurring.setDescription(request.getDescription());

        if (request.getPaymentMethod() != null)
            recurring.setPaymentMethod(request.getPaymentMethod());

        if (request.getFrequency() != null)
            recurring.setFrequency(request.getFrequency());

        if (request.getNextOccurrence() != null)
            recurring.setNextOccurrence(
                    request.getNextOccurrence()
            );

        if (request.getEndDate() != null)
            recurring.setEndDate(request.getEndDate());

        if (request.getActive() != null)
            recurring.setActive(request.getActive());

        recurring.setUpdatedAt(LocalDateTime.now());

        RecurringTransaction saved =
                recurringTransactionRepository.save(recurring);

        return mapToResponse(saved);
    }

    private RecurringTransactionResponse mapToResponse(
            RecurringTransaction recurring) {

        return RecurringTransactionResponse.builder()
                .id(recurring.getId())
                .accountId(recurring.getAccountId())
                .categoryId(recurring.getCategoryId())
                .amount(recurring.getAmount())
                .type(recurring.getType())
                .merchant(recurring.getMerchant())
                .description(recurring.getDescription())
                .paymentMethod(recurring.getPaymentMethod())
                .frequency(recurring.getFrequency())
                .nextOccurrence(
                        recurring.getNextOccurrence())
                .endDate(recurring.getEndDate())
                .active(recurring.isActive())
                .build();
    }
}