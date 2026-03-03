package com.valentin_d.focusarc.fixtures.user;

import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.valentin_d.focusarc.helpers.TestConstants.NOW;

@Builder
public class UserBuilder {
    @Builder.Default
    private final UserId id = UserId.random();
    @Builder.Default
    private final String name = "John Doe";
    @Builder.Default
    private final String email = "test@test.com";
    @Builder.Default
    private final LocalDateTime lastLogin = NOW;
    @Builder.Default
    private final String password = "password123";

    public User build() {
        return new User(id, name, email, lastLogin, password);
    }

    public static UserBuilderBuilder from(User user) {
        return UserBuilder.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail());
    }
}