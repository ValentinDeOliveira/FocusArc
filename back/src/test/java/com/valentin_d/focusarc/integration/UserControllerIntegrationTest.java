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
    @Test
    void shouldReturnUser_whenIdExists() {
        final var response = request(URL, HttpMethod.GET, getHttpEntity(), User.class);

        assertionHelper.assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);
        assertGetUserEquals(result, user);
    }

    @Test
    void getUserById_returnsNotFound_whenNotFound() {
        final var response = request(URL + "/" +  UserId.random().id(), HttpMethod.GET,
                getHttpEntity(), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @ParameterizedTest
    @MethodSource("provideUserUpdateDtos")
    void shouldUpdateUser_withDifferentFields(final UserUpdateDto dto) {
        final var response = request(URL, HttpMethod.PUT, getHttpEntity(dto), User.class);

        assertionHelper.assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(result.getId(), user.getId());
        assertEquals(assertionHelper.expectedValue(dto.name(), user.getName()), result.getName());
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

        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.PUT,
                getHttpEntity(dto), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteUser_whenIdExists() {
        final var response = request(URL, HttpMethod.DELETE,
                getHttpEntity(), Void.class);

        assertionHelper.assertNoContent(response);
    }
}