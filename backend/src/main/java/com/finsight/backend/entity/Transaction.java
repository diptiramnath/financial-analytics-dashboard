package com.finsight.backend.entity;

import com.finsight.backend.enums.PaymentMethod;
import com.finsight.backend.enums.TransactionStatus;
import com.finsight.backend.enums.TransactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection="transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    private String id;
    private String userId;
    private String accountId;
    private String categoryId;
    private BigDecimal amount;
    private TransactionType type;
    private String merchant;
    private String description;
    private LocalDateTime transactionDate;
    private PaymentMethod paymentMethod;
    private String location;
    private String notes;
    private String receiptUrl;
    private boolean recurring;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
