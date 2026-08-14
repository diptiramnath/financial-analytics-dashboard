package com.finsight.backend.entity;

import com.finsight.backend.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private String userId;
    private String code;
    private String name;
    private CategoryType type;
    private String icon;
    private String color;
    private boolean systemCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}