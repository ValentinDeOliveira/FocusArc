package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.fixtures.user.UserBuilder;
import com.valentin_d.focusarc.fixtures.user.UserCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.user.UserUpdateDtoBuilder;
import com.valentin_d.focusarc.integration.base.BaseUserControllerIntegrationTest;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerIntegrationTest extends BaseUserControllerIntegrationTest {
    @Test
    void shouldCreateUser_whenDataIsValid() {
        final var dto = UserCreationDtoBuilder.builder().build().build();

        final var response = request(URL, HttpMethod.POST, dto, User.class);

        assertCreated(response);

        final var result = response.getBody();
        assertNotNull(result);
        assertEquals(dto.email(), result.getEmail());
        assertEquals(dto.name(), result.getName());
        assertNotNull(result.getId());
        assertNotNull(result.getLastLogin());
    }

    @Test
    void shouldReturnConflict_whenEmailAlreadyExists() {
        final var user = createUser();
        final var dto = UserCreationDtoBuilder.builder().email(user.getEmail()).build().build();

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertConflict(response);
    }

    @Test
    void shouldReturnUser_whenEmailExists() {
        final var user = createUser();
        final var response = request(URL + "/email?email="+ user.getEmail(), HttpMethod.GET, null, User.class);

        assertHasContent(response);

        final var result = response.getBody();
        assertNotNull(result);
        assertGetUserEquals(result, user);
    }

    @Test
    void shouldReturnNotFound_whenEmailDoesNotExist() {
        final var response = request(URL + "/email?email=foobar@test.com", HttpMethod.GET, null, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnUser_whenIdExists() {
        final var user = createUser();
        final var response = request(URL + "/" + user.getId().id(), HttpMethod.GET, null, User.class);

        assertHasContent(response);

        final var result = response.getBody();
        assertNotNull(result);
        assertGetUserEquals(result, user);
    }

    @Test
    void getUserById_returnsNotFound_whenNotFound() {
        final var response = request(URL + "/" +  UserId.random().id(), HttpMethod.GET, null, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExist() {
        final var user = createUser();
        final var dto = UserUpdateDtoBuilder.builder().build().build();

        final var response = request(URL+ "/" + user.getId().id(), HttpMethod.PUT, dto, User.class);

        assertHasContent(response);

        final var result = response.getBody();
        assertNotNull(result);
        final var expected = UserBuilder.from(user).name(dto.name()).build().build();

        assertGetUserEquals(expected, result);
    }

    @Test
    void shouldReturnNotFoundOnUpdate_whenIdDoesNotExists() {
        final var dto = UserUpdateDtoBuilder.builder().build().build();

        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.PUT, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteUser_whenIdExists() {
        final var user = createUser();
        final var response = request(URL + "/" + user.getId().id(), HttpMethod.DELETE, null, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingUser() {
        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.DELETE, null, Void.class);

        assertNotFound(response);
    }
}