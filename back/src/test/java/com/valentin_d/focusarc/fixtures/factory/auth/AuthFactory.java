package com.valentin_d.focusarc.fixtures.factory.auth;

import com.valentin_d.focusarc.fixtures.auth.LoginDtoBuilder;
import com.valentin_d.focusarc.fixtures.auth.RefreshRequestDtoBuilder;
import com.valentin_d.focusarc.model.auth.LoginDto;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;

public final class AuthFactory {
    private AuthFactory() {}

    public static LoginDto aLoginDto() {
        return LoginDtoBuilder.builder().build().build();
    }

    public static LoginDto aLoginDtoWithMailAndPassword(final String mail, final String password) {
        return LoginDtoBuilder.builder().email(mail).password(password).build().build();
    }

    public static RefreshRequestDto aRefreshRequestDto() {
        return RefreshRequestDtoBuilder.builder().build().build();
    }

    public static RefreshRequestDto aRefreshRequestDtoWithToken(final String token) {
        return RefreshRequestDtoBuilder.builder().refreshToken(token).build().build();
    }
}