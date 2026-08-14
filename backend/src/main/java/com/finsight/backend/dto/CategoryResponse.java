package com.finsight.backend.dto;
import com.finsight.backend.enums.CategoryType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private String id;
    private String code;
    private String name;
    private CategoryType type;
    private String icon;
    private String color;
    private boolean systemCategory;
}