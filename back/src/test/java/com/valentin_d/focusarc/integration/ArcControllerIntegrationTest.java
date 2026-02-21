package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.fixtures.arc.ArcCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcUpdateDtoBuilder;
import com.valentin_d.focusarc.integration.base.BaseArcControllerIntegrationTest;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ArcControllerIntegrationTest extends BaseArcControllerIntegrationTest {
    @Test
    void shouldCreateArc_whenDataIsValid() {
        final var user = createUser();

        final var dto = ArcCreationDtoBuilder.builder().userId(user.getId()).build().build();
        final var response = request(URL, HttpMethod.POST, dto, Arc.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        final Arc arc = response.getBody();

        assertEquals(dto.totalPlannedMinutes(), arc.getTotalPlannedMinutes());
        assertEquals(arc.getOwner(), user.getId());
        assertEquals(dto.name(), arc.getName());
        assertEquals(0, arc.getTotalCompletedMinutes());
        assertNotNull(arc.getId());
    }

    @Test
    void shouldReturnNotFoundOnCreate_whenUserDoesNotExists() {
        final var dto = ArcCreationDtoBuilder.builder().build().build();

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnArc_whenIdExists() {
        final var arc = createArc();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.GET, null, Arc.class);

        assertHasContent(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertArcEquals(result, arc);
    }

    @Test
    void shouldReturnAllArc_whenUserIdExists() {
        final var user = createUser();
        final var arc1 = createArcForUser(user.getId());
        final var arc2 = createArcForUser(user.getId());

        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.GET, null, Arc[].class);

        assertHasContent(response);
        final List<Arc> arcs = Arrays.stream(response.getBody()).toList();
        assertNotNull(arcs);
        assertEquals(2, arcs.size());
        assertThatCollection(arcs).containsExactly(arc1, arc2);
    }

    @Test
    void shouldReturnNotFound_whenUserIdDoesNotExists() {
        final var response = request(URL + "/users/" + UserId.random().id(), HttpMethod.GET, null, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnNotFound_whenUserIdExistsWithoutArc() {
        final var user = createUser();
        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.GET, null, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldUpdateArc_whenIdExists() {
        final var arc = createArc();

        final var dto = ArcUpdateDtoBuilder.builder().build().build();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.PUT, dto, Arc.class);

        assertHasContent(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(result.getId(), arc.getId());
        assertEquals(result.getName(), dto.name());
        assertEquals(result.getOwner(), arc.getOwner());
        assertEquals(result.getTotalPlannedMinutes(), dto.totalPlannedMinutes());
        assertEquals(result.getTotalCompletedMinutes(), arc.getTotalCompletedMinutes());
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingArc() {
        final var dto = ArcUpdateDtoBuilder.builder().build().build();

        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.PUT, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteArc_whenIdExists() {
        final var arc = createArc();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.DELETE, null, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingArc() {
        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.DELETE, null, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteAllArcForUser_whenUserIdExists() {
        final var user = createUser();
        createArcForUser(user.getId());
        createArcForUser(user.getId());

        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.DELETE, null, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllArcForNonExistingUser() {
        final var response = request(URL + "/users/" + UserId.random().id(), HttpMethod.DELETE, null, Void.class);

        assertNotFound(response);
    }
}