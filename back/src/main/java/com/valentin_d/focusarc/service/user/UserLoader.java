package com.valentin_d.focusarc.service.user;

import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.UserRepository;
import com.valentin_d.focusarc.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLoader extends BaseService {
    private final UserRepository userRepository;

    public void assertUserExists(final UserId userId) {
        existsOrThrow(userRepository, userId, () -> new UserDoesNotExistException(userId));
    }
}