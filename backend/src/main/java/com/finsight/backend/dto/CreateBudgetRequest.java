package com.finsight.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBudgetRequest {

    @NotNull
    private String categoryId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;
}