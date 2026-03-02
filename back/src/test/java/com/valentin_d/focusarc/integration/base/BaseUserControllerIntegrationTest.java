package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseUserControllerIntegrationTest extends BaseIntegrationTest{
    @Autowired
    protected UserRepository userRepository;
    protected final String URL = "/users";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // clean db before each test
    }

    protected void assertGetUserEquals(final User expected, final User actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertNotNull(expected.getLastLogin());
    }

    protected User createUser() {
        final var user = aUser();
        return userRepository.save(user);
    }
}