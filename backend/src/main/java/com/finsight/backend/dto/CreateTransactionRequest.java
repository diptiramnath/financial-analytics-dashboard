package com.finsight.backend.dto;

import com.finsight.backend.enums.PaymentMethod;
import com.finsight.backend.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransactionRequest {

    @NotNull
    private String accountId;

    @NotNull
    private String categoryId;

    @Positive
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    private String merchant;

    private String description;

    @NotNull
    private LocalDateTime transactionDate;

    private PaymentMethod paymentMethod;

    private String notes;
}
