package com.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username Is Required")
    private String username;
    @NotBlank(message = "Password Is Required")
    private String password;
}
