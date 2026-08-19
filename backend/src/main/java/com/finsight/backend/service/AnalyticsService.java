package com.finsight.backend.service;

import com.finsight.backend.dto.CategoryExpenseResponse;
import com.finsight.backend.dto.MonthlyAnalyticsResponse;
import com.finsight.backend.dto.MonthlyComparisonResponse;
import com.finsight.backend.entity.Category;
import com.finsight.backend.entity.Transaction;
import com.finsight.backend.entity.User;
import com.finsight.backend.enums.TransactionType;
import com.finsight.backend.repository.CategoryRepository;
import com.finsight.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public AnalyticsService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            CurrentUserService currentUserService) {

        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.currentUserService = currentUserService;
    }

    public MonthlyAnalyticsResponse getMonthlyAnalytics(
            int month,
            int year) {

        User currentUser =
                currentUserService.getCurrentUser();

        LocalDateTime start =
                LocalDateTime.of(
                        year,
                        month,
                        1,
                        0,
                        0
                );

        LocalDateTime end =
                start.plusMonths(1)
                        .minusNanos(1);

        List<Transaction> transactions =
                transactionRepository
                        .findByUserIdAndTransactionDateBetween(
                                currentUser.getId(),
                                start,
                                end
                        );

        BigDecimal totalIncome =
                transactions.stream()
                        .filter(t ->
                                t.getType()
                                        == TransactionType.INCOME)
                        .map(Transaction::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalExpenses =
                transactions.stream()
                        .filter(t ->
                                t.getType()
                                        == TransactionType.EXPENSE)
                        .map(Transaction::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal netSavings =
                totalIncome.subtract(totalExpenses);

        List<CategoryExpenseResponse>
                expensesByCategory =
                calculateExpensesByCategory(
                        transactions
                );

        return MonthlyAnalyticsResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .expensesByCategory(
                        expensesByCategory
                )
                .build();
    }

    private List<CategoryExpenseResponse>
    calculateExpensesByCategory(
            List<Transaction> transactions) {

        Map<String, BigDecimal> totals =
                new HashMap<>();

        for (Transaction transaction : transactions) {

            if (transaction.getType()
                    != TransactionType.EXPENSE) {

                continue;
            }

            totals.merge(
                    transaction.getCategoryId(),
                    transaction.getAmount(),
                    BigDecimal::add
            );
        }

        return totals.entrySet()
                .stream()
                .map(entry -> {

                    Category category =
                            categoryRepository
                                    .findById(entry.getKey())
                                    .orElse(null);

                    return CategoryExpenseResponse
                            .builder()
                            .categoryId(entry.getKey())
                            .categoryName(
                                    category != null
                                            ? category.getName()
                                            : "Unknown"
                            )
                            .amount(entry.getValue())
                            .build();
                })
                .toList();
    }

    public MonthlyComparisonResponse getMonthlyComparison(
            int month,
            int year) {

        User currentUser =
                currentUserService.getCurrentUser();

        LocalDateTime currentStart =
                LocalDateTime.of(
                        year,
                        month,
                        1,
                        0,
                        0
                );

        LocalDateTime currentEnd =
                currentStart
                        .plusMonths(1)
                        .minusNanos(1);

        LocalDateTime previousStart =
                currentStart.minusMonths(1);

        LocalDateTime previousEnd =
                currentStart.minusNanos(1);

        List<Transaction> currentTransactions =
                transactionRepository
                        .findByUserIdAndTransactionDateBetween(
                                currentUser.getId(),
                                currentStart,
                                currentEnd
                        );

        List<Transaction> previousTransactions =
                transactionRepository
                        .findByUserIdAndTransactionDateBetween(
                                currentUser.getId(),
                                previousStart,
                                previousEnd
                        );

        BigDecimal currentIncome =
                calculateTotal(
                        currentTransactions,
                        TransactionType.INCOME
                );

        BigDecimal currentExpenses =
                calculateTotal(
                        currentTransactions,
                        TransactionType.EXPENSE
                );

        BigDecimal previousIncome =
                calculateTotal(
                        previousTransactions,
                        TransactionType.INCOME
                );

        BigDecimal previousExpenses =
                calculateTotal(
                        previousTransactions,
                        TransactionType.EXPENSE
                );

        BigDecimal currentSavings =
                currentIncome.subtract(currentExpenses);

        BigDecimal previousSavings =
                previousIncome.subtract(previousExpenses);

        return MonthlyComparisonResponse.builder()
                .currentIncome(currentIncome)
                .previousIncome(previousIncome)
                .incomeChangePercentage(
                        calculatePercentageChange(
                                previousIncome,
                                currentIncome
                        )
                )
                .currentExpenses(currentExpenses)
                .previousExpenses(previousExpenses)
                .expensesChangePercentage(
                        calculatePercentageChange(
                                previousExpenses,
                                currentExpenses
                        )
                )
                .currentSavings(currentSavings)
                .previousSavings(previousSavings)
                .savingsChangePercentage(
                        calculatePercentageChange(
                                previousSavings,
                                currentSavings
                        )
                )
                .build();
    }

    private BigDecimal calculateTotal(
            List<Transaction> transactions,
            TransactionType type) {

        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private double calculatePercentageChange(
            BigDecimal previous,
            BigDecimal current) {

        if (previous.compareTo(BigDecimal.ZERO) == 0) {

            if (current.compareTo(BigDecimal.ZERO) == 0) {
                return 0;
            }

            return 100;
        }

        return current.subtract(previous)
                .divide(
                        previous,
                        4,
                        java.math.RoundingMode.HALF_UP
                )
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}