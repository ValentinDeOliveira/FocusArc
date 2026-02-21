package com.valentin_d.focusarc.fixtures.user;

import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import lombok.Builder;

@Builder
public class UserBuilder {
    @Builder.Default
    private final UserId id = UserId.random();
    @Builder.Default
    private final String name = "John Doe";
    @Builder.Default
    private final String email = "test@test.com";

    public User build() {
        return new User(id, name, email);
    }
}