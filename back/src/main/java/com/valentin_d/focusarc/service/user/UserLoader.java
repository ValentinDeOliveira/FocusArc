package com.valentin_d.focusarc.service.user;

import com.valentin_d.focusarc.exception.EmailAlreadyExistsException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.repository.UserRepository;
import com.valentin_d.focusarc.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserLoader extends BaseService {
    private final UserRepository userRepository;

    public User getUserIfExists(UserId userId) {
        return fetchOrThrow(userRepository, userId, () -> new UserDoesNotExistException(userId));
    }

    public void assertUserExists(UserId userId) {
        existsOrThrow(userRepository, userId, () -> new UserDoesNotExistException(userId));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void assertEmailDoNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}