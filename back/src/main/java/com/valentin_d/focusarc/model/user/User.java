package com.valentin_d.focusarc.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.valentin_d.focusarc.model.id.UserId;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
public class User implements UserDetails {
    @Id
    private UserId id;
    private String name;
    @Indexed(unique = true)
    private String email;
    // TODO: change this field to Instant
    private LocalDateTime lastLogin;
    // TODO: add field for timezone
    @Getter(AccessLevel.NONE)
    private String password;
    private AuthProvider authProvider;

    public User(final String name, final String email, final AuthProvider authProvider) {
        this(UserId.random(), name, email,  LocalDateTime.now(),null, authProvider);
    }

    public User(final String name, final String email, final String password, final AuthProvider authProvider) {
        this(UserId.random(), name, email, LocalDateTime.now(), password, authProvider);
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return email;
    }
}