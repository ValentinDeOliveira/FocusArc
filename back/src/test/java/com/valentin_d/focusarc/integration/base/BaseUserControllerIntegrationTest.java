package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseUserControllerIntegrationTest extends BaseIntegrationTest{
    @Autowired
    protected UserRepository userRepository;
    protected final String URL = "/users";

    protected static final String USER_NAME = "foobar";
    protected static final String USER_EMAIL = "foo@test.com";
    protected static final UserId USER_ID = UserId.random();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // clean db before each test
        userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL));
    }

    protected void assertGetUserEquals(final User actual) {
        assertEquals(USER_ID, actual.getId());
        assertEquals(USER_NAME, actual.getName());
        assertEquals(USER_EMAIL, actual.getEmail());
        assertNotNull(actual.getLastLogin());
    }

    protected void assertGetUserEquals(final User actual, final User expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getName(), expected.getName());
        assertEquals(actual.getEmail(), expected.getEmail());
        assertNotNull(actual.getLastLogin());
    }
}