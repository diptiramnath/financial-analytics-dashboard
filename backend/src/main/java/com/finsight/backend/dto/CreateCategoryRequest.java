package com.finsight.backend.dto;

import com.finsight.backend.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private CategoryType type;

    private String icon;

    private String color;
}