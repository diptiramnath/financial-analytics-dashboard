package com.finsight.backend.dto;

import com.finsight.backend.enums.PaymentMethod;
import com.finsight.backend.enums.RecurringFrequency;
import com.finsight.backend.enums.TransactionType;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecurringTransactionRequest {

    private String accountId;

    private String categoryId;

    @Positive
    private BigDecimal amount;

    private TransactionType type;

    private String merchant;

    private String description;

    private PaymentMethod paymentMethod;

    private RecurringFrequency frequency;

    private LocalDateTime nextOccurrence;

    private LocalDateTime endDate;

    private Boolean active;
}