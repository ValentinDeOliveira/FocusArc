package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import com.valentin_d.focusarc.model.auth.LoginDto;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;
import com.valentin_d.focusarc.model.auth.RegisterRequestDto;
import com.valentin_d.focusarc.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Public endpoints — no Bearer token required")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register a new account")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody final RegisterRequestDto dto) {
        final var user = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Authenticate with email and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody final LoginDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(summary = "Exchange a refresh token for a new token pair")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody final RefreshRequestDto dto) {
        return ResponseEntity.ok(authService.refresh(dto));
    }
}
