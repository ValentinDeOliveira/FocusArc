package com.valentin_d.focusarc.fixtures.factory.auth;

import com.valentin_d.focusarc.fixtures.auth.GoogleAuthRequestDtoBuilder;
import com.valentin_d.focusarc.fixtures.auth.LoginDtoBuilder;
import com.valentin_d.focusarc.fixtures.auth.RefreshRequestDtoBuilder;
import com.valentin_d.focusarc.model.auth.GoogleAuthRequestDto;
import com.valentin_d.focusarc.model.auth.LoginRequestDto;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;

public final class AuthFactory {
    private AuthFactory() {}

    public static LoginRequestDto aLoginDto() {
        return LoginDtoBuilder.builder().build().build();
    }

    public static LoginRequestDto aLoginDtoWithMailAndPassword(final String mail, final String password) {
        return LoginDtoBuilder.builder().email(mail).password(password).build().build();
    }

    public static RefreshRequestDto aRefreshRequestDto() {
        return RefreshRequestDtoBuilder.builder().build().build();
    }

    public static RefreshRequestDto aRefreshRequestDtoWithToken(final String token) {
        return RefreshRequestDtoBuilder.builder().refreshToken(token).build().build();
    }

    public static GoogleAuthRequestDto aGoogleAuthRequestDto() {
        return GoogleAuthRequestDtoBuilder.builder().build().build();
    }

    public static GoogleAuthRequestDto aGoogleAuthRequestDtoWithToken(final String idToken) {
        return GoogleAuthRequestDtoBuilder.builder().idToken(idToken).build().build();
    }
}