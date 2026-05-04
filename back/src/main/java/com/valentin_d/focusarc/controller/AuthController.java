package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.model.auth.GoogleAuthRequestDto;
import com.valentin_d.focusarc.model.auth.LoginRequestDto;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;
import com.valentin_d.focusarc.model.auth.RegisterRequestDto;
import com.valentin_d.focusarc.service.auth.AuthService;
import com.valentin_d.focusarc.service.auth.CookieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final CookieService cookieService;

    @Operation(summary = "Register a new account")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody final RegisterRequestDto dto,
                                         final HttpServletResponse response) {
        cookieService.setAuthCookies(response, authService.register(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Authenticate with email and password")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody final LoginRequestDto dto,
                                      final HttpServletResponse response) {
        cookieService.setAuthCookies(response, authService.login(dto));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Exchange a refresh token for a new token pair")
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(final HttpServletRequest request,
                                        final HttpServletResponse response) {
        final var refreshToken = cookieService.extractCookieValue(request, CookieService.REFRESH_TOKEN);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        cookieService.setAuthCookies(response, authService.refresh(new RefreshRequestDto(refreshToken)));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Authenticate or register with a Google ID token")
    @PostMapping("/google")
    public ResponseEntity<Void> google(@Valid @RequestBody final GoogleAuthRequestDto dto,
                                       final HttpServletResponse response) {
        cookieService.setAuthCookies(response, authService.loginWithGoogle(dto));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Invalidate session cookies")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        cookieService.clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }
}