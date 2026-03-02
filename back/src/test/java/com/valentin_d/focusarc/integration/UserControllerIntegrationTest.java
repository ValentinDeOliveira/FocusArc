package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseUserControllerIntegrationTest;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerIntegrationTest extends BaseUserControllerIntegrationTest {
    /*@Test
    void shouldCreateUser_whenDataIsValid() {
        final var dto = aUserCreationDto();

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
        final var dto = aUserCreationDtoWithEmail(user.getEmail());

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertConflict(response);
    }*/

    @Test
    void shouldReturnUser_whenEmailExists() {
        final var user = createUser();
        final var response = request(URL + "/email?email="+ user.getEmail(), HttpMethod.GET, User.class);

        assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);
        assertGetUserEquals(result, user);
    }

    @Test
    void shouldReturnNotFound_whenEmailDoesNotExist() {
        final var response = request(URL + "/email?email=foobar@test.com", HttpMethod.GET, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnUser_whenIdExists() {
        final var user = createUser();
        final var response = request(URL + "/" + user.getId().id(), HttpMethod.GET, User.class);

        assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);
        assertGetUserEquals(result, user);
    }

    @Test
    void getUserById_returnsNotFound_whenNotFound() {
        final var response = request(URL + "/" +  UserId.random().id(), HttpMethod.GET, Void.class);

        assertNotFound(response);
    }

    @ParameterizedTest
    @MethodSource("provideUserUpdateDtos")
    void shouldUpdateUser_withDifferentFields(final UserUpdateDto dto) {
        final var user = createUser();

        final var response = request(URL+ "/" + user.getId().id(), HttpMethod.PUT, dto, User.class);

        assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(result.getId(), user.getId());
        assertEquals(expectedValue(dto.name(), user.getName()), result.getName());
        assertEquals(result.getEmail(), user.getEmail());
        assertNotNull(result.getLastLogin());
    }

    private static Stream<Arguments> provideUserUpdateDtos() {
        return Stream.of(
                Arguments.of(aUserUpdateDtoWithName("Updated Name")),
                Arguments.of(aUserUpdateDtoWithNullFields())
        );
    }

    @Test
    void shouldReturnNotFoundOnUpdate_whenIdDoesNotExists() {
        final var dto = aUserUpdateDto();

        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.PUT, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteUser_whenIdExists() {
        final var user = createUser();
        final var response = request(URL + "/" + user.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingUser() {
        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }
}