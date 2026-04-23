package com.valentin_d.focusarc.model.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequestDto(@NotBlank String idToken) {}