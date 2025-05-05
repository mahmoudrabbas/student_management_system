package com.system.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.system.config.SanitizingDeserializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username Is Required")
    @JsonDeserialize(using = SanitizingDeserializer.class)
    private String username;
    @NotBlank(message = "Password Is Required")
    @JsonDeserialize(using = SanitizingDeserializer.class)
    private String password;
}
