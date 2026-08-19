package com.finsight.backend.dto;

import com.finsight.backend.enums.PaymentMethod;
import com.finsight.backend.enums.RecurringFrequency;
import com.finsight.backend.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecurringTransactionRequest {

    @NotNull
    private String accountId;

    @NotNull
    private String categoryId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    private String merchant;

    private String description;

    private PaymentMethod paymentMethod;

    @NotNull
    private RecurringFrequency frequency;

    @NotNull
    private LocalDateTime nextOccurrence;

    private LocalDateTime endDate;
}