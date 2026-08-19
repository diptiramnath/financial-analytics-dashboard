package com.finsight.backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyComparisonResponse {

    private BigDecimal currentIncome;
    private BigDecimal previousIncome;
    private double incomeChangePercentage;

    private BigDecimal currentExpenses;
    private BigDecimal previousExpenses;
    private double expensesChangePercentage;

    private BigDecimal currentSavings;
    private BigDecimal previousSavings;
    private double savingsChangePercentage;
}