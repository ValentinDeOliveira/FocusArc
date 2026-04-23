package com.valentin_d.focusarc.fixtures.auth;

import com.valentin_d.focusarc.model.auth.GoogleAuthRequestDto;
import lombok.Builder;

@Builder
public class GoogleAuthRequestDtoBuilder {
    @Builder.Default
    private final String idToken = "google-token";

    public GoogleAuthRequestDto build() {
        return new GoogleAuthRequestDto(idToken);
    }
}