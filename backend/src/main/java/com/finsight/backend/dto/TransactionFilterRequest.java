package com.finsight.backend.dto;

import com.finsight.backend.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionFilterRequest {

    private String accountId;
    private String categoryId;
    private TransactionType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}