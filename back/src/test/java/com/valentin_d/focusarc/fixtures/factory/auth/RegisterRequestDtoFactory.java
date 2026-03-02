package com.valentin_d.focusarc.fixtures.factory.auth;

import com.valentin_d.focusarc.fixtures.auth.RegisterRequestDtoBuilder;
import com.valentin_d.focusarc.model.auth.RegisterRequestDto;

public final class RegisterRequestDtoFactory {
    private RegisterRequestDtoFactory() {}

    public static RegisterRequestDto aRegisterRequestDto() {
        return RegisterRequestDtoBuilder.builder().build().build();
    }
}