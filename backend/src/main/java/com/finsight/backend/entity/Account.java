package com.finsight.backend.entity;

import com.finsight.backend.enums.AccountType;
import com.finsight.backend.enums.Currency;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection="accounts")
public class Account {
    @Id
    private String id;
    private String userId;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private Currency currency;
    private String institution;
    private String maskedAccountNumber;
    private String theme;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
