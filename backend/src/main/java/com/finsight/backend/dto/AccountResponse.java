package com.finsight.backend.dto;

import com.finsight.backend.enums.AccountType;
import com.finsight.backend.enums.Currency;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse{
    private String id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private Currency currency;
    private String institution;
    private String maskedAccountNumber;
}
