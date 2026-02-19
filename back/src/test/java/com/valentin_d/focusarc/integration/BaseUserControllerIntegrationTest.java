package com.valentin_d.focusarc.integration;

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
    protected static final String URL = BASE_URL + "/users";

    protected static final String USER_NAME = "foobar";
    protected static final String USER_EMAIL = "foo@test.com";
    protected static final UserId USER_ID = UserId.random();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // clean db before each test
        userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL));
    }

    protected void assertGetUserEquals(final User expected) {
        assertEquals(USER_ID, expected.getId());
        assertEquals(USER_NAME, expected.getName());
        assertEquals(USER_EMAIL, expected.getEmail());
        assertNotNull(expected.getLastLogin());
    }
}