package com.valentin_d.focusarc.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreationDto(@NotBlank String name, @NotBlank @Email String email) {
}
