package com.valentin_d.focusarc.fixtures.auth;

import com.valentin_d.focusarc.model.auth.RegisterRequestDto;
import lombok.Builder;

@Builder
public class RegisterRequestDtoBuilder {
    @Builder.Default
    private final String name = "foobar";
    @Builder.Default
    private final String email = "foobar@test.com";
    @Builder.Default
    private final String password = "password123";

    public RegisterRequestDto build() {
        return new RegisterRequestDto(name, email, password);
    }
}