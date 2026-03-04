package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseArcControllerIntegrationTest;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
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
        final var user = domainFixture.user();
        final var dto = anArcCreationDto();

        final var headers = getHeadersForUser(user);

        final var requestEntity = new HttpEntity<>(dto, headers);

        final var response = request(URL, HttpMethod.POST, requestEntity, Arc.class);

        assertionHelper.assertCreated(response);

        final var arc = response.getBody();
        assertNotNull(arc);
        assertEquals(dto.totalEstimatedMinutes(), arc.getTotalEstimatedMinutes());
        assertEquals(arc.getOwner(), user.getId());
        assertEquals(dto.name(), arc.getName());
        assertEquals(0, arc.getTotalCompletedMinutes());
        assertNotNull(arc.getId());
    }

    @Test
    void shouldReturnNotFoundOnCreate_whenActiveArcAlreadyExists() {
        final var user = domainFixture.user();
        domainFixture.arcForUser(user.getId());
        final var dto = anArcCreationDto();

        final var headers = getHeadersForUser(user);

        final HttpEntity<ArcCreationDto> requestEntity = new HttpEntity<>(dto, headers);

        final var response = request(URL, HttpMethod.POST, requestEntity, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnArc_whenIdExists() {
        final var arc = domainFixture.arcWithUser();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.GET, Arc.class);
        assertionHelper.assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertArcEquals(result, arc);
    }

    @Test
    void shouldReturnAllArc_whenUserIdExists() {
        final var user = domainFixture.user();
        final var arc1 = domainFixture.arcForUser(user.getId());
        final var arc2 = domainFixture.arcForUser(user.getId(), ArcStatus.COMPLETED);

        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.GET, Arc[].class);
        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());

        final List<Arc> arcs = Arrays.stream(response.getBody()).toList();
        assertNotNull(arcs);
        assertEquals(2, arcs.size());
        assertThatCollection(arcs).containsExactly(arc1, arc2);
    }

    @Test
    void shouldReturnNotFound_whenUserIdDoesNotExists() {
        final var response = request(URL + "/users/" + UserId.random().id(), HttpMethod.GET, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnNoContent_whenUserHasNoArcs() {
        final var user = domainFixture.user();
        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.GET, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideArcUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final ArcUpdateDto dto) {
        final var arc = domainFixture.arcWithUser();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.PUT, dto, Arc.class);

        assertionHelper.assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(arc.getId(), result.getId());
        assertEquals(arc.getOwner(), result.getOwner());
        assertEquals(assertionHelper.expectedValue(dto.name(), arc.getName()), result.getName());
        assertEquals(assertionHelper.expectedValue(dto.totalEstimatedMinutes(), arc.getTotalEstimatedMinutes()),
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

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteArc_whenIdExists() {
        final var arc = domainFixture.arcWithUser();

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingArc() {
        final var response = request(URL + "/" + UserId.random().id(), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteAllArcForUser_whenUserIdExists() {
        final var user = domainFixture.user();
        domainFixture.arcForUser(user.getId());
        domainFixture.arcForUser(user.getId(), ArcStatus.COMPLETED);

        final var response = request(URL + "/users/" + user.getId().id(), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllArcForNonExistingUser() {
        final var response = request(URL + "/users/" + UserId.random().id(), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTotalEstimatedMinutes")
    void shouldReturnBadRequestOnCreate_whenTotalEstimatedMinutesIsNotPositive(final int minutes) {
        final var user = domainFixture.user();
        final var dto = anArcCreationDtoWithEstimatedMinutes(minutes);
        final var headers = getHeadersForUser(user);
        final var response = request(URL, HttpMethod.POST, new HttpEntity<>(dto, headers), Void.class);
        assertionHelper.assertBadRequest(response);
    }

    private static Stream<Arguments> provideInvalidTotalEstimatedMinutes() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-1)
        );
    }
}