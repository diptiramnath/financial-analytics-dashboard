package com.finsight.backend.repository;

import com.finsight.backend.entity.Category;
import com.finsight.backend.enums.CategoryType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findByUserId(String userId);

    List<Category> findBySystemCategoryTrueOrUserId(
            String userId
    );

    List<Category> findByType(CategoryType type);

    List<Category> findByUserIdAndType(
            String userId,
            CategoryType type
    );

    Optional<Category> findByIdAndUserId(
            String id,
            String userId
    );

    Optional<Category> findByCode(String code);

    boolean existsByNameAndUserId(
            String name,
            String userId
    );
}