package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseArcControllerIntegrationTest;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ArcControllerIntegrationTest extends BaseArcControllerIntegrationTest {
    @Test
    void shouldCreateArc_whenDataIsValid() {
        final var user = createUser();

        final var dto = anArcCreationDtoWithOwnerId(user.getId());
        final var response = request(URL, HttpMethod.POST, dto, Arc.class);

        assertCreated(response);

        final var arc = response.getBody();
        assertNotNull(arc);
        assertEquals(dto.totalEstimatedMinutes(), arc.getTotalEstimatedMinutes());
        assertEquals(arc.getOwner(), user.getId());
        assertEquals(dto.name(), arc.getName());
        assertEquals(0, arc.getTotalCompletedMinutes());
        assertNotNull(arc.getId());
    }

    @Test
    void shouldReturnNotFoundOnCreate_whenUserDoesNotExists() {
        final var dto = anArcCreationDto();

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnArc_whenIdExists() {
        final var arc = createArc();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.GET, Arc.class);
        assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertArcEquals(result, arc);
    }

    @Test
    void shouldReturnAllArc_whenUserIdExists() {
        final var user = createUser();
        final var arc1 = createArcForUser(user.getId());
        final var arc2 = createArcForUser(user.getId());

        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.GET, Arc[].class);
        assertOk(response);
        assertNotNull(response.getBody());

        final List<Arc> arcs = Arrays.stream(response.getBody()).toList();
        assertNotNull(arcs);
        assertEquals(2, arcs.size());
        assertThatCollection(arcs).containsExactly(arc1, arc2);
    }

    @Test
    void shouldReturnNotFound_whenUserIdDoesNotExists() {
        final var response = request(URL + "/users/" + UserId.random().id(), HttpMethod.GET, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnNotFound_whenUserIdExistsWithoutArc() {
        final var user = createUser();
        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.GET, Void.class);

        assertNotFound(response);
    }

    @ParameterizedTest
    @MethodSource("provideArcUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final ArcUpdateDto dto) {
        final var arc = createArc();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.PUT, dto, Arc.class);

        assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(arc.getId(), result.getId());
        assertEquals(arc.getOwner(), result.getOwner());
        assertEquals(expectedValue(dto.name(), arc.getName()), result.getName());
        assertEquals(expectedValue(dto.totalEstimatedMinutes(), arc.getTotalEstimatedMinutes()),
                result.getTotalEstimatedMinutes());
        assertEquals(arc.getTotalCompletedMinutes(), result.getTotalCompletedMinutes());
    }

    private static Stream<Arguments> provideArcUpdateDtos() {
        return Stream.of(
                Arguments.of(anArcUpdateDtoWithTotalEstimatedMinutesAndName(200, "Updated Name")),
                Arguments.of(anArcUpdateDtoWithName("Updated Name")),
                Arguments.of(anArcUpdateDtoWithTotalEstimatedMinutes(150)),
                Arguments.of(anArcUpdateDtoWithNullFields())
        );
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingArc() {
        final var dto = anArcUpdateDto();

        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.PUT, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteArc_whenIdExists() {
        final var arc = createArc();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingArc() {
        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteAllArcForUser_whenUserIdExists() {
        final var user = createUser();
        createArcForUser(user.getId());
        createArcForUser(user.getId());

        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllArcForNonExistingUser() {
        final var response = request(URL + "/users/" + UserId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }
}