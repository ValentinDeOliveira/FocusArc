package com.valentin_d.focusarc.fixtures.user;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import lombok.Builder;

@Builder
public class UserCreationDtoBuilder {
    @Builder.Default
    private final String name = "John Doe";
    @Builder.Default
    private final String email = "test@test.com";

    public UserCreationDto build() {
        return new UserCreationDto(name, email);
    }
}