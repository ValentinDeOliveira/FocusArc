package com.valentin_d.focusarc.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDto(@NotBlank String name) {
}