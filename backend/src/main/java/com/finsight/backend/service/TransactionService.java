package com.finsight.backend.service;

import com.finsight.backend.dto.CreateTransactionRequest;
import com.finsight.backend.dto.TransactionResponse;
import com.finsight.backend.entity.Account;
import com.finsight.backend.entity.Transaction;
import com.finsight.backend.entity.User;
import com.finsight.backend.enums.TransactionStatus;
import com.finsight.backend.enums.TransactionType;
import com.finsight.backend.exception.AccountNotFoundException;
import com.finsight.backend.repository.AccountRepository;
import com.finsight.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.finsight.backend.exception.TransactionNotFoundException;
import com.finsight.backend.dto.TransactionFilterRequest;
import com.finsight.backend.enums.TransactionType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;
    private final MongoTemplate mongoTemplate;

    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            CurrentUserService currentUserService, MongoTemplate mongoTemplate) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
        this.mongoTemplate = mongoTemplate;
    }

    private void applyTransactionEffect(
            Account account,
            TransactionType type,
            BigDecimal amount) {

        if (type == TransactionType.INCOME) {

            account.setBalance(
                    account.getBalance().add(amount)
            );

        } else if (type == TransactionType.EXPENSE) {

            account.setBalance(
                    account.getBalance().subtract(amount)
            );
        }
    }

    private void reverseTransactionEffect(
            Account account,
            TransactionType type,
            BigDecimal amount) {

        if (type == TransactionType.INCOME) {

            account.setBalance(
                    account.getBalance().subtract(amount)
            );

        } else if (type == TransactionType.EXPENSE) {

            account.setBalance(
                    account.getBalance().add(amount)
            );
        }
    }

    public TransactionResponse createTransaction(
            CreateTransactionRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        Transaction savedTransaction =
                createTransactionInternal(
                        currentUser.getId(),
                        request.getAccountId(),
                        request.getCategoryId(),
                        request.getAmount(),
                        request.getType(),
                        request.getMerchant(),
                        request.getDescription(),
                        request.getTransactionDate(),
                        request.getPaymentMethod(),
                        request.getNotes(),
                        false
                );

        return mapToResponse(savedTransaction);
    }

    public List<TransactionResponse> getTransactions() {

        User currentUser = currentUserService.getCurrentUser();

        return transactionRepository
                .findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TransactionResponse getTransaction(String id) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository
                .findByIdAndUserId(
                        id,
                        currentUser.getId()
                )
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found"
                        )
                );

        return mapToResponse(transaction);
    }

    public TransactionResponse updateTransaction(
            String id,
            CreateTransactionRequest request) {

        User currentUser = currentUserService.getCurrentUser();
        Transaction transaction = transactionRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found"
                        ));

        Account oldAccount = accountRepository
                .findByIdAndUserId(
                        transaction.getAccountId(),
                        currentUser.getId()
                )
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found"
                        ));

        reverseTransactionEffect(
                oldAccount,
                transaction.getType(),
                transaction.getAmount()
        );

        Account newAccount = accountRepository
                .findByIdAndUserId(
                        request.getAccountId(),
                        currentUser.getId()
                )
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found"
                        ));

        applyTransactionEffect(
                newAccount,
                request.getType(),
                request.getAmount()
        );

        transaction.setAccountId(request.getAccountId());
        transaction.setCategoryId(request.getCategoryId());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setMerchant(request.getMerchant());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setNotes(request.getNotes());
        transaction.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(oldAccount);
        accountRepository.save(newAccount);
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    public void deleteTransaction(String id) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found"
                        ));

        Account account = accountRepository
                .findByIdAndUserId(
                        transaction.getAccountId(),
                        currentUser.getId()
                )
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found"
                        ));

        reverseTransactionEffect(
                account,
                transaction.getType(),
                transaction.getAmount()
        );

        accountRepository.save(account);

        transactionRepository.delete(transaction);
    }

    public List<TransactionResponse> getTransactions(
            TransactionFilterRequest filter) {

        User currentUser = currentUserService.getCurrentUser();

        Query query = new Query();

        query.addCriteria(
                Criteria.where("userId")
                        .is(currentUser.getId())
        );

        if (filter.getAccountId() != null) {
            query.addCriteria(
                    Criteria.where("accountId")
                            .is(filter.getAccountId())
            );
        }

        if (filter.getCategoryId() != null) {
            query.addCriteria(
                    Criteria.where("categoryId")
                            .is(filter.getCategoryId())
            );
        }

        if (filter.getType() != null) {
            query.addCriteria(
                    Criteria.where("type")
                            .is(filter.getType())
            );
        }

        if (filter.getStartDate() != null
                && filter.getEndDate() != null) {

            query.addCriteria(
                    Criteria.where("transactionDate")
                            .gte(filter.getStartDate())
                            .lte(filter.getEndDate())
            );
        }

        return mongoTemplate
                .find(query, Transaction.class)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    Transaction createTransactionInternal(
            String userId,
            String accountId,
            String categoryId,
            BigDecimal amount,
            TransactionType type,
            String merchant,
            String description,
            LocalDateTime transactionDate,
            com.finsight.backend.enums.PaymentMethod paymentMethod,
            String notes,
            boolean recurring) {

        Account account = accountRepository
                .findByIdAndUserId(accountId, userId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found"
                        ));

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .accountId(account.getId())
                .categoryId(categoryId)
                .amount(amount)
                .type(type)
                .merchant(merchant)
                .description(description)
                .transactionDate(transactionDate)
                .paymentMethod(paymentMethod)
                .notes(notes)
                .status(TransactionStatus.COMPLETED)
                .recurring(recurring)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        applyTransactionEffect(
                account,
                type,
                amount
        );

        accountRepository.save(account);

        return transactionRepository.save(transaction);
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