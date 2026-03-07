package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseArcControllerIntegrationTest;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArcControllerIntegrationTest extends BaseArcControllerIntegrationTest {
    @Test
    void shouldCreateArc_whenDataIsValid() {
        final var dto = anArcCreationDto();

        final var response = request(URL, HttpMethod.POST, new HttpEntity<>(dto, userHeaders), Arc.class);

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
        domainFixture.arcForUser(user.getId());

        final HttpEntity<ArcCreationDto> requestEntity = new HttpEntity<>(anArcCreationDto(), userHeaders);

        final var response = request(URL, HttpMethod.POST, requestEntity, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnArc_whenIdExists() {
        final var arc = domainFixture.arcForUser(user.getId());

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.GET,
                new HttpEntity<>(userHeaders), Arc.class);

        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());
        assertArcEquals(response.getBody(), arc);
    }

    @Test
    void shouldReturnAllArc_whenUserIdExists() {
        final var arc1 = domainFixture.arcForUser(user.getId());
        final var arc2 = domainFixture.arcForUser(user.getId(), ArcStatus.COMPLETED);

        final var response = request(URL + "/me", HttpMethod.GET, new HttpEntity<>(userHeaders), Arc[].class);

        assertionHelper.assertOk(response);
        assertThat(response.getBody()).containsExactly(arc1, arc2);
    }

    @Test
    void shouldReturnNoContent_whenUserHasNoArcs() {
        final var response = request(URL + "/me", HttpMethod.GET, new HttpEntity<>(userHeaders), Void.class);

        assertionHelper.assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideArcUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final ArcUpdateDto dto) {
        final var arc = domainFixture.arcForUser(user.getId());

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.PUT,
                new HttpEntity<>(dto, userHeaders), Arc.class);

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
        final var response = request(URL + "/" + ArcId.random().id(), HttpMethod.PUT,
                new HttpEntity<>(anArcUpdateDto(), userHeaders), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteArc_whenIdExists() {
        final var arc = domainFixture.arcForUser(user.getId());

        final var response = request(URL + "/" + arc.getId().id(), HttpMethod.DELETE,
                new HttpEntity<>(userHeaders), Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingArc() {
        final var response = request(URL + "/" + ArcId.random().id(), HttpMethod.DELETE,
                new HttpEntity<>(userHeaders), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteAllArcForUser_whenUserIdExists() {
        domainFixture.arcForUser(user.getId());
        domainFixture.arcForUser(user.getId(), ArcStatus.COMPLETED);

        final var response = request(URL, HttpMethod.DELETE, new HttpEntity<>(userHeaders), Void.class);

        assertionHelper.assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTotalEstimatedMinutes")
    void shouldReturnBadRequestOnCreate_whenTotalEstimatedMinutesIsNotPositive(final int minutes) {
        final var response = request(URL, HttpMethod.POST,
                new HttpEntity<>(anArcCreationDtoWithEstimatedMinutes(minutes), userHeaders), Void.class);

        assertionHelper.assertBadRequest(response);
    }

    private static Stream<Arguments> provideInvalidTotalEstimatedMinutes() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-1)
        );
    }
}