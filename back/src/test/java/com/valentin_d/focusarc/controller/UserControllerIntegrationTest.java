package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.integration.BaseUserControllerIntegrationTest;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerIntegrationTest extends BaseUserControllerIntegrationTest {
    @Test
    void createUser_returnsCreatedUser() {
        final var dto = new UserCreationDto("Alice", "alice@example.com");

        final var response = request(URL, HttpMethod.POST, dto, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        final User user = response.getBody();

        assertEquals("alice@example.com", user.getEmail());
        assertEquals("Alice", user.getName());
        assertNotNull(user.getId());
        assertNotNull(user.getLastLogin());
    }

    @Test
    void createUser_withExistingMail_shouldFail() {
        final var dto = new UserCreationDto("foo", "foo@test.com");

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUserByEmail_returnsUser_whenFound() {
        final var response = request(URL + "/email?email="+ USER_EMAIL, HttpMethod.GET, null, User.class);

        assertHasContent(response);

        final User user = response.getBody();

        assertGetUserEquals(user);
    }

    @Test
    void getUserByEmail_returnsNotFound_whenNotFound() {
        final var response = request(URL + "/email?email=bar@test.com", HttpMethod.GET, null, User.class);

        assertNotFound(response);
    }

    @Test
    void getUserById_returnsUser_whenFound() {
        final var response = request(URL + "/" + USER_ID.id(), HttpMethod.GET, null, User.class);

        assertHasContent(response);

        final User user = response.getBody();

        assertGetUserEquals(user);
    }

    @Test
    void getUserById_returnsNotFound_whenNotFound() {
        final var response = request(URL + "/" +  UserId.random().id(), HttpMethod.GET, null, User.class);

        assertNotFound(response);
    }

    @Test
    void updateUser_withNewFields_whenFound() {
        final var dto = new UserUpdateDto("bar");

        final var response = request(URL+ "/" + USER_ID.id(), HttpMethod.PUT, dto, User.class);

        assertHasContent(response);

        final var user = response.getBody();
        final var expected = new User(USER_ID, "bar", USER_EMAIL);

        assertGetUserEquals(user, expected);
    }

    @Test
    void updateUser_withNewFields_whenNotFound() {
        final var dto = new UserUpdateDto("bar");

        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.PUT, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void deleteUser_whenFound() {
        final var response = request(URL + "/" + USER_ID.id(), HttpMethod.DELETE, null, Void.class);

        assertNoContent(response);
    }

    @Test
    void deleteUser_whenNotFound() {
        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.DELETE, null, Void.class);

        assertNotFound(response);
    }
}