package com.finsight.backend.service;

import com.finsight.backend.dto.BudgetResponse;
import com.finsight.backend.dto.CreateBudgetRequest;
import com.finsight.backend.entity.Budget;
import com.finsight.backend.entity.Category;
import com.finsight.backend.entity.Transaction;
import com.finsight.backend.entity.User;
import com.finsight.backend.enums.NotificationType;
import com.finsight.backend.enums.TransactionType;
import com.finsight.backend.exception.CategoryNotFoundException;
import com.finsight.backend.repository.BudgetRepository;
import com.finsight.backend.repository.CategoryRepository;
import com.finsight.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public BudgetService(
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            CurrentUserService currentUserService, NotificationService notificationService) {

        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
    }

    public BudgetResponse createBudget(
            CreateBudgetRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .filter(c ->
                        c.isSystemCategory()
                                || currentUser.getId()
                                .equals(c.getUserId()))
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found"
                        ));

        if (budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(
                        currentUser.getId(),
                        request.getCategoryId(),
                        request.getMonth(),
                        request.getYear()
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Budget already exists for this category and month"
            );
        }

        Budget budget = Budget.builder()
                .userId(currentUser.getId())
                .categoryId(category.getId())
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Budget saved = budgetRepository.save(budget);

        return mapToResponse(saved);
    }

    public List<BudgetResponse> getBudgets(
            int month,
            int year) {

        User currentUser =
                currentUserService.getCurrentUser();

        return budgetRepository
                .findByUserIdAndMonthAndYear(
                        currentUser.getId(),
                        month,
                        year
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BudgetResponse mapToResponse(
            Budget budget) {

        Category category = categoryRepository
                .findById(budget.getCategoryId())
                .orElse(null);

        BigDecimal spent =
                calculateSpent(budget);

        BigDecimal remaining =
                budget.getAmount().subtract(spent);

        double percentageUsed = 0;

        if (percentageUsed >= 100) {

            notificationService.createNotification(
                    currentUserService.getCurrentUser().getId(),
                    NotificationType.BUDGET_EXCEEDED,
                    "Budget exceeded",
                    "You've exceeded your "
                            + category.getName()
                            + " budget.",
                    budget.getId(),
                    "BUDGET_EXCEEDED_"
                            + budget.getId()
                            + "_"
                            + budget.getMonth()
                            + "_"
                            + budget.getYear()
            );

        } else if (percentageUsed >= 80) {

            notificationService.createNotification(
                    currentUserService.getCurrentUser().getId(),
                    NotificationType.BUDGET_WARNING,
                    "Budget almost reached",
                    "You've used "
                            + String.format(
                            "%.1f",
                            percentageUsed
                    )
                            + "% of your "
                            + category.getName()
                            + " budget.",
                    budget.getId(),
                    "BUDGET_WARNING_"
                            + budget.getId()
                            + "_"
                            + budget.getMonth()
                            + "_"
                            + budget.getYear()
            );
        }

        if (budget.getAmount()
                .compareTo(BigDecimal.ZERO) > 0) {

            percentageUsed =
                    spent.doubleValue()
                            / budget.getAmount().doubleValue()
                            * 100;
        }

        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategoryId())
                .categoryName(
                        category != null
                                ? category.getName()
                                : "Unknown"
                )
                .amount(budget.getAmount())
                .spent(spent)
                .remaining(remaining)
                .percentageUsed(percentageUsed)
                .month(budget.getMonth())
                .year(budget.getYear())
                .active(budget.isActive())
                .build();
    }

    private BigDecimal calculateSpent(
            Budget budget) {

        User currentUser =
                currentUserService.getCurrentUser();

        LocalDateTime start =
                LocalDateTime.of(
                        budget.getYear(),
                        budget.getMonth(),
                        1,
                        0,
                        0
                );

        LocalDateTime end =
                start.plusMonths(1).minusNanos(1);

        List<Transaction> transactions =
                transactionRepository
                        .findByUserIdAndTransactionDateBetween(
                                currentUser.getId(),
                                start,
                                end
                        );

        return transactions.stream()
                .filter(t ->
                        t.getType()
                                == TransactionType.EXPENSE)
                .filter(t ->
                        budget.getCategoryId()
                                .equals(t.getCategoryId()))
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}