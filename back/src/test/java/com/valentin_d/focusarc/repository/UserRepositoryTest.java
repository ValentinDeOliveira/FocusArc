package com.valentin_d.focusarc.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    void shouldReturnUser_whenEmailExists() {
        final var user = aUser();
        repository.save(user);

        final var userOptional = repository.findByEmail(user.getEmail());
        assertThat(userOptional.isPresent()).isTrue();
        final var foundUser = userOptional.get();
        assertEquals(foundUser.getEmail(), user.getEmail());
        assertEquals(foundUser.getName(), user.getName());
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }
}