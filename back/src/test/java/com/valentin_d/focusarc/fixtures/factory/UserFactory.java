package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.fixtures.user.UserBuilder;
import com.valentin_d.focusarc.fixtures.user.UserUpdateDtoBuilder;
import com.valentin_d.focusarc.model.user.User;

public final class UserFactory {
    private UserFactory() {}

    public static User aUser() {
        return UserBuilder.builder().build().build();
    }

    public static User aUserWithEmailAndPassword(final String email, final String password) {
        return UserBuilder.builder().email(email).password(password).build().build();
    }

    public static UserUpdateDto aUserUpdateDto() {
        return UserUpdateDtoBuilder.builder().build().build();
    }

    public static UserUpdateDto aUserUpdateDtoWithName(final String name) {
        return UserUpdateDtoBuilder.builder().name(name).build().build();
    }

    public static UserUpdateDto aUserUpdateDtoWithNullFields() {
        return UserUpdateDtoBuilder.builder().name(null).build().build();
    }
}