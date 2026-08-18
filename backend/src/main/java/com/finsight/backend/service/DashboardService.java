package com.finsight.backend.service;

import com.finsight.backend.dto.CategoryExpenseResponse;
import com.finsight.backend.dto.DashboardResponse;
import com.finsight.backend.dto.TransactionResponse;
import com.finsight.backend.entity.Account;
import com.finsight.backend.entity.Category;
import com.finsight.backend.entity.Transaction;
import com.finsight.backend.entity.User;
import com.finsight.backend.enums.TransactionType;
import com.finsight.backend.repository.AccountRepository;
import com.finsight.backend.repository.CategoryRepository;
import com.finsight.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public DashboardService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            CurrentUserService currentUserService) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.currentUserService = currentUserService;
    }

    public DashboardResponse getDashboard() {

        User currentUser = currentUserService.getCurrentUser();

        List<Account> accounts =
                accountRepository.findByUserId(currentUser.getId());

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Transaction> transactions =
                transactionRepository.findByUserId(currentUser.getId());

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSavings =
                totalIncome.subtract(totalExpenses);

        List<CategoryExpenseResponse> expensesByCategory =
                calculateExpensesByCategory(
                        transactions,
                        currentUser.getId()
                );

        List<TransactionResponse> recentTransactions =
                transactions.stream()
                        .sorted(
                                Comparator.comparing(
                                        Transaction::getTransactionDate
                                ).reversed()
                        )
                        .limit(5)
                        .map(this::mapToResponse)
                        .toList();

        return DashboardResponse.builder()
                .totalBalance(totalBalance)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .expensesByCategory(expensesByCategory)
                .recentTransactions(recentTransactions)
                .build();
    }

    private List<CategoryExpenseResponse> calculateExpensesByCategory(
            List<Transaction> transactions,
            String userId) {

        Map<String, BigDecimal> categoryTotals =
                new HashMap<>();

        for (Transaction transaction : transactions) {

            if (transaction.getType() != TransactionType.EXPENSE) {
                continue;
            }

            categoryTotals.merge(
                    transaction.getCategoryId(),
                    transaction.getAmount(),
                    BigDecimal::add
            );
        }

        return categoryTotals.entrySet()
                .stream()
                .map(entry -> {

                    Category category =
                            categoryRepository
                                    .findById(entry.getKey())
                                    .orElse(null);

                    String categoryName =
                            category != null
                                    ? category.getName()
                                    : "Unknown";

                    return CategoryExpenseResponse.builder()
                            .categoryId(entry.getKey())
                            .categoryName(categoryName)
                            .amount(entry.getValue())
                            .build();
                })
                .toList();
    }

    private TransactionResponse mapToResponse(
            Transaction transaction) {

        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .categoryId(transaction.getCategoryId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .merchant(transaction.getMerchant())
                .description(transaction.getDescription())
                .date(transaction.getTransactionDate())
                .method(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .build();
    }
}