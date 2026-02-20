package com.valentin_d.focusarc.model;

import com.valentin_d.focusarc.model.id.UserId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
public class User {
    @Id
    private UserId id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private LocalDateTime lastLogin;

    public User(final UserId userId, final String name, final String email) {
        this(userId, name, email, LocalDateTime.now());
    }

    public User(final String name, final String email) {
        this(UserId.random(), name, email);
    }
}