package com.finsight.backend.entity;

import com.finsight.backend.enums.Currency;
import com.finsight.backend.enums.Language;
import com.finsight.backend.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private Currency currency;
    private String country;
    private Role role;
    private String timezone;
    private String profilePictureUrl;
    private Language preferredLanguage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
