package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import com.valentin_d.focusarc.model.auth.LoginDto;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;
import com.valentin_d.focusarc.model.auth.RegisterRequestDto;
import com.valentin_d.focusarc.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody final RegisterRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody final LoginDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody final RefreshRequestDto dto) {
        return ResponseEntity.ok(authService.refresh(dto));
    }
}
