package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User create(final UserCreationDto userDto) {
        final var user = new User(userDto.name(), userDto.email());

        return repository.save(user);
    }
}
