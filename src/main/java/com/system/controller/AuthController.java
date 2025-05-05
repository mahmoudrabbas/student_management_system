package com.system.controller;

import com.system.dto.LoginRequest;
import com.system.dto.RefreshRequest;
import com.system.entity.User;
import com.system.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "sign up", description = "register one user to website")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        authService.register(user);
        return ResponseEntity.ok().body("Successfully User Registered");
    }

    @Operation(summary = "Login", description = "Authenticate user and return access and refresh tokens")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok().body(authService.login(request));
    }

    @Operation(summary = "logout", description = "log out from system")
    @ApiResponse(responseCode = "204", description = "Successfully logged out")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshRequest refreshToken){
        authService.logout(refreshToken);
        return ResponseEntity.ok().body("Successfully Logged out");
    }

    @Operation(summary = "refresh Token", description = "refresh new Access token")
    @ApiResponse(responseCode = "200", description = "refresh the access token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshToken){
        return ResponseEntity.ok().body(authService.refreshAccess(refreshToken));
    }





}
