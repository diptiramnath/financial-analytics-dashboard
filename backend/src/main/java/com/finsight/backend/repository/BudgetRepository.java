package com.finsight.backend.repository;

import com.finsight.backend.entity.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository
        extends MongoRepository<Budget, String> {

    List<Budget> findByUserId(String userId);

    List<Budget> findByUserIdAndMonthAndYear(
            String userId,
            int month,
            int year
    );

    Optional<Budget> findByIdAndUserId(
            String id,
            String userId
    );

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            String userId,
            String categoryId,
            int month,
            int year
    );
}