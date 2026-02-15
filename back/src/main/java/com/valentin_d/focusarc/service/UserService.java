package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.exception.EmailAlreadyExistsException;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.repository.UserRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User create(final UserCreationDto userDto) {
        getByEmail(userDto.email()).ifPresent(user -> {
            throw new EmailAlreadyExistsException(userDto.email());
        });

        final var user = new User(userDto.name(), userDto.email());

        return repository.save(user);
    }

    public Optional<User> getByEmail(@Email final String email) {
        return repository.findByEmail(email);
    }
}
