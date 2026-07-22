package com.finsight.backend.dto;

import com.finsight.backend.enums.AccountType;
import com.finsight.backend.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotBlank
    private String name;

    @NotNull
    private AccountType type;

    @NotNull
    private BigDecimal balance;

    @NotNull
    private Currency currency;

    private String institution;
    private String maskedAccountNumber;
}
