package com.valentin_d.focusarc.fixtures.factory.auth;

import com.valentin_d.focusarc.fixtures.auth.AuthResponseDtoBuilder;
import com.valentin_d.focusarc.model.auth.AuthResponseDto;

public final class AuthResponseDtoFactory {
    private AuthResponseDtoFactory() {}

    public static AuthResponseDto anAuthResponseDto() {
        return AuthResponseDtoBuilder.builder().build().build();
    }
}