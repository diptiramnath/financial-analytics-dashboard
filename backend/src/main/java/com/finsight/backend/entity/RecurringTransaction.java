package com.finsight.backend.entity;

import com.finsight.backend.enums.PaymentMethod;
import com.finsight.backend.enums.RecurringFrequency;
import com.finsight.backend.enums.TransactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "recurringTransactions")
public class RecurringTransaction {

    @Id
    private String id;

    private String userId;

    private String accountId;

    private String categoryId;

    private BigDecimal amount;

    private TransactionType type;

    private String merchant;

    private String description;

    private PaymentMethod paymentMethod;

    private RecurringFrequency frequency;

    private LocalDateTime nextOccurrence;

    private LocalDateTime endDate;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}