package com.valentin_d.focusarc.service.user;

import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.auth.RegisterRequestDto;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserLoader userLoader;
    private final PasswordEncoder passwordEncoder;

    public User create(@NotNull final RegisterRequestDto registerDto) {
        userLoader.assertEmailDoNotExist(registerDto.email());

        final var user = new User(registerDto.name(), registerDto.email(),
                passwordEncoder.encode(registerDto.password()));

        return repository.save(user);
    }

    public Optional<User> findByEmail(@NotBlank @Email final String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findById(@NotNull final UserId id) {
        return repository.findById(id);
    }

    public User update(@NotNull final UserId id, @NotNull final UserUpdateDto dto) {
        final var user = userLoader.getUserIfExists(id);

        if (dto.name() != null) user.setName(dto.name());

        return repository.save(user);
    }

    public void delete(@NotNull final UserId id) {
        final var user = userLoader.getUserIfExists(id);
        repository.delete(user);
    }
}