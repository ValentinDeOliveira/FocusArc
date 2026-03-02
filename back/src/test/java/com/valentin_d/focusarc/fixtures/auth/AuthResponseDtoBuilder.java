package com.valentin_d.focusarc.fixtures.auth;

import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import lombok.Builder;

@Builder
public class AuthResponseDtoBuilder {
    @Builder.Default
    private final String accessToken = "accessToken";
    @Builder.Default
    private final String refreshToken = "refreshToken";

    public AuthResponseDto build() {
        return new AuthResponseDto(accessToken, refreshToken);
    }
}