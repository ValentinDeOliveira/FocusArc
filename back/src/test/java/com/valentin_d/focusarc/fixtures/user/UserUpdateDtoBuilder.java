package com.valentin_d.focusarc.fixtures.user;

import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import lombok.Builder;

@Builder
public class UserUpdateDtoBuilder {
    @Builder.Default
    private final String name = "John Doe updated";

    public UserUpdateDto build() {
        return new UserUpdateDto(name);
    }
}