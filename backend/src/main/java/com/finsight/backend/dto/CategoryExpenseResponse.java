package com.finsight.backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryExpenseResponse {

    private String categoryId;
    private String categoryName;
    private BigDecimal amount;
}