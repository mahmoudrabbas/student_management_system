package com.system.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.system.config.SanitizingDeserializer;
import lombok.Data;

@Data
public class RefreshRequest {
    @JsonDeserialize(using = SanitizingDeserializer.class)
    private String refreshToken;
}
