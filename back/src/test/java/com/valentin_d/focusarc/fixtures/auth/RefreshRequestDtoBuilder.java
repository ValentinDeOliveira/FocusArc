package com.valentin_d.focusarc.fixtures.auth;

import com.valentin_d.focusarc.model.auth.RefreshRequestDto;
import lombok.Builder;

@Builder
public class RefreshRequestDtoBuilder {
    @Builder.Default
    private final String refreshToken = "refreshToken";

    public RefreshRequestDto build() {
        return new RefreshRequestDto(refreshToken);
    }
}