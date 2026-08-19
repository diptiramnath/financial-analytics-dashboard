package com.finsight.backend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "budgets")
public class Budget {

    @Id
    private String id;

    private String userId;

    private String categoryId;

    private BigDecimal amount;

    private int month;

    private int year;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}