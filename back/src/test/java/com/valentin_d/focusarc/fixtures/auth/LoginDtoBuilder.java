package com.valentin_d.focusarc.fixtures.auth;

import com.valentin_d.focusarc.model.auth.LoginRequestDto;
import lombok.Builder;

@Builder
public class LoginDtoBuilder {
    @Builder.Default
    private final String email = "test@test.com";
    @Builder.Default
    private final String password = "password123";

    public LoginRequestDto build() {
        return new LoginRequestDto(email, password);
    }
}