package com.finsight.backend.service;

import com.finsight.backend.dto.NotificationResponse;
import com.finsight.backend.entity.Notification;
import com.finsight.backend.entity.User;
import com.finsight.backend.enums.NotificationType;
import com.finsight.backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    public NotificationService(
            NotificationRepository notificationRepository,
            CurrentUserService currentUserService) {

        this.notificationRepository = notificationRepository;
        this.currentUserService = currentUserService;
    }

    public Notification createNotification(
            String userId,
            NotificationType type,
            String title,
            String message,
            String referenceId,
            String referenceKey) {

        Optional<Notification> existing =
                notificationRepository
                        .findByUserIdAndReferenceKey(
                                userId,
                                referenceKey
                        );

        if (existing.isPresent()) {
            return existing.get();
        }

        Notification notification =
                Notification.builder()
                        .userId(userId)
                        .type(type)
                        .title(title)
                        .message(message)
                        .referenceId(referenceId)
                        .referenceKey(referenceKey)
                        .read(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        return notificationRepository.save(
                notification
        );
    }

    public List<NotificationResponse> getNotifications() {

        User currentUser =
                currentUserService.getCurrentUser();

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        currentUser.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<NotificationResponse> getUnreadNotifications() {

        User currentUser =
                currentUserService.getCurrentUser();

        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                        currentUser.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void markAsRead(String id) {

        User currentUser =
                currentUserService.getCurrentUser();

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                ));

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    public void markAllAsRead() {

        User currentUser =
                currentUserService.getCurrentUser();

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                                currentUser.getId()
                        );

        notifications.forEach(
                notification -> notification.setRead(true)
        );

        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}