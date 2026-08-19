package com.finsight.backend.repository;

import com.finsight.backend.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends MongoRepository<Notification, String> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(
            String userId
    );

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(
            String userId
    );

    Optional<Notification> findByIdAndUserId(
            String id,
            String userId
    );

    Optional<Notification> findByUserIdAndReferenceKey(
            String userId,
            String referenceKey
    );
}