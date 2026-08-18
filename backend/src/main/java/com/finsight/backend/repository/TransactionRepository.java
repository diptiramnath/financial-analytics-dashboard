package com.finsight.backend.repository;

import com.finsight.backend.entity.Transaction;
import com.finsight.backend.enums.TransactionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction,String> {
    List<Transaction> findByUserId(String userId);
    Optional<Transaction> findByIdAndUserId(String id, String userId);
    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByCategoryId(String id);
    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    List<Transaction> findByUserIdAndType(
            String userId,
            TransactionType type
    );

    List<Transaction> findByUserIdAndCategoryId(
            String userId,
            String categoryId
    );

    List<Transaction> findByUserIdAndTransactionDateBetween(
            String userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Transaction> findByUserIdAndTypeAndCategoryId(
            String userId,
            TransactionType type,
            String categoryId
    );

    List<Transaction> findByUserIdAndTransactionDateBetweenAndType(
            String userId,
            LocalDateTime start,
            LocalDateTime end,
            TransactionType type
    );
}
