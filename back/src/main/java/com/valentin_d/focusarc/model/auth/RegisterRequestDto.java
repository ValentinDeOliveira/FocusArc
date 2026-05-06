package com.valentin_d.focusarc.model.auth;

import com.valentin_d.focusarc.util.validation.ValidEmail;
import com.valentin_d.focusarc.util.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank @Size(min = 3, max = 50) String name,
        @ValidEmail String email,
        @ValidPassword String password
) {}