package com.finsight.backend.dto;

import com.finsight.backend.enums.InsightType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsightResponse {

    private InsightType type;

    private String title;

    private String message;

    private String severity;
}