package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.fixtures.user.UserBuilder;
import com.valentin_d.focusarc.fixtures.user.UserCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.user.UserUpdateDtoBuilder;
import com.valentin_d.focusarc.model.User;

public final class UserFactory {
    private UserFactory() {}

    public static User aUser() {
        return UserBuilder.builder().build().build();
    }

    public static UserCreationDto aUserCreationDto() {
        return UserCreationDtoBuilder.builder().build().build();
    }

    public static UserCreationDto aUserCreationDtoWithEmail(final String email) {
        return UserCreationDtoBuilder.builder().email(email).build().build();
    }

    public static UserUpdateDto aUserUpdateDto() {
        return UserUpdateDtoBuilder.builder().build().build();
    }
}