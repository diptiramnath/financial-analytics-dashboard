package com.finsight.backend.dto;

import com.finsight.backend.enums.Currency;
import com.finsight.backend.enums.Language;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Currency currency;
    private String country;
    private Language preferredLanguage;
    private String timezone;
    private String profilePictureUrl;
}
