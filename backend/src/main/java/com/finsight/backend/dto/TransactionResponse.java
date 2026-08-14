package com.finsight.backend.dto;

import com.finsight.backend.enums.PaymentMethod;
import com.finsight.backend.enums.TransactionStatus;
import com.finsight.backend.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private String id;
    private String accountId;
    private String categoryId;
    private TransactionType type;
    private BigDecimal amount;
    private String merchant;
    private String description;
    private LocalDateTime date;
    private PaymentMethod method;
    private TransactionStatus status;
}
