package com.finsight.backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {

    private String id;

    private String categoryId;

    private String categoryName;

    private BigDecimal amount;

    private BigDecimal spent;

    private BigDecimal remaining;

    private double percentageUsed;

    private int month;

    private int year;

    private boolean active;
}