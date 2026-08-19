package com.finsight.backend.repository;

import com.finsight.backend.entity.RecurringTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository
        extends MongoRepository<RecurringTransaction, String> {

    List<RecurringTransaction> findByUserId(String userId);

    Optional<RecurringTransaction> findByIdAndUserId(
            String id,
            String userId
    );

    List<RecurringTransaction> findByUserIdAndActive(
            String userId,
            boolean active
    );

    List<RecurringTransaction>
    findByActiveTrueAndNextOccurrenceLessThanEqual(
            LocalDateTime date
    );
}