package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseArcControllerIntegrationTest;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArcControllerIntegrationTest extends BaseArcControllerIntegrationTest {
    @Test
    void shouldCreateArc_whenDataIsValid() {
        final var dto = anArcCreationDto();

        final var response = request(URL, HttpMethod.POST, dto, Arc.class);

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

        final var dto  = anArcCreationDto();

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnArc_whenIdExists() {
        final var arc = domainFixture.arcForUser(user.getId());

        final var response = request(arcUrl(arc.getId()), HttpMethod.GET, Arc.class);

        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());
        assertArcEquals(response.getBody(), arc);
    }

    @Test
    void shouldReturnAllArc_whenUserIdExists() {
        final var arc1 = domainFixture.arcForUser(user.getId());
        final var arc2 = domainFixture.arcForUser(user.getId(), ArcStatus.COMPLETED);

        final var response = request(URL + "/me", HttpMethod.GET,  Arc[].class);

        assertionHelper.assertOk(response);
        assertThat(response.getBody()).containsExactly(arc1, arc2);
    }

    @Test
    void shouldReturnNoContent_whenUserHasNoArcs() {
        final var response = request(URL + "/me", HttpMethod.GET,  Void.class);

        assertionHelper.assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideArcUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final ArcUpdateDto dto) {
        final var arc = domainFixture.arcForUser(user.getId());

        final var response = request(arcUrl(arc.getId()), HttpMethod.PUT,
                dto, Arc.class);

        assertionHelper.assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(arc.getId(), result.getId());
        assertEquals(arc.getOwner(), result.getOwner());
        assertEquals(assertionHelper.expectedValue(dto.name(), arc.getName()), result.getName());
        assertEquals(assertionHelper.expectedValue(dto.totalEstimatedMinutes(), arc.getTotalEstimatedMinutes()),
                result.getTotalEstimatedMinutes());
        assertEquals(arc.getTotalCompletedMinutes(), result.getTotalCompletedMinutes());
        assertEquals(assertionHelper.expectedValue(dto.startDate(), arc.getStartDate()), result.getStartDate());
        assertEquals(assertionHelper.expectedValue(dto.endDate(), arc.getEndDate()), result.getEndDate());
    }

    private static Stream<Arguments> provideArcUpdateDtos() {
        return Stream.of(
                Arguments.of(anArcUpdateDtoWithAllUpdatedFields(200, "Updated Name", LocalDate.now(), LocalDate.now().plusDays(15))),
                Arguments.of(anArcUpdateDtoWithName("Updated Name")),
                Arguments.of(anArcUpdateDtoWithTotalEstimatedMinutes(150)),
                Arguments.of(anArcUpdateDtoWithStartDate(LocalDate.now())),
                Arguments.of(anArcUpdateDtoWithEndDate(LocalDate.now().plusDays(20))),
                Arguments.of(anArcUpdateDtoWithNullFields())
        );
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingArc() {
        final var response = request(arcUrl(ArcId.random()), HttpMethod.PUT,
                anArcUpdateDto(), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteArc_whenIdExists() {
        final var arc = domainFixture.arcForUser(user.getId());

        final var response = request(arcUrl(arc.getId()), HttpMethod.DELETE,
                 Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingArc() {
        final var response = request(arcUrl(ArcId.random()), HttpMethod.DELETE,
                 Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteAllArcForUser_whenUserIdExists() {
        domainFixture.arcForUser(user.getId());
        domainFixture.arcForUser(user.getId(), ArcStatus.COMPLETED);

        final var response = request(URL, HttpMethod.DELETE, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTotalEstimatedMinutes")
    void shouldReturnBadRequestOnCreate_whenTotalEstimatedMinutesIsNotPositive(final int minutes) {
        final var response = request(URL, HttpMethod.POST,
                anArcCreationDtoWithEstimatedMinutes(minutes), Void.class);

        assertionHelper.assertBadRequest(response);
    }

    private static Stream<Arguments> provideInvalidTotalEstimatedMinutes() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-1)
        );
    }

    @Test
    void shouldReturnSummary_whenUserHasActiveArc() {
        final var arc = domainFixture.arcForUser(user.getId());
        domainFixture.chapterForArcWithDate(arc.getId(), LocalDate.now().minusDays(1)); // COMPLETED
        domainFixture.chapterForArcWithDate(arc.getId(), LocalDate.now().minusDays(2)); // COMPLETED
        domainFixture.plannedChapterForArcWithDate(arc.getId(), LocalDate.now().plusDays(1)); // PLANNED

        final var response = request(URL + "/summary", HttpMethod.GET, ArcSummaryResponseDto.class);

        assertionHelper.assertOk(response);
        final var summary = response.getBody();
        assertNotNull(summary);
        assertEquals(summary.arcId(), arc.getId());
        assertEquals(arc.getTotalEstimatedMinutes(), summary.totalEstimatedMinutes());
        assertEquals(2, summary.nbChapterCompleted());
        assertEquals(1, summary.nbChapterPlanned());
        assertEquals(0, summary.nbChapterSkipped());
        assertEquals(2, summary.daysStreak());
    }

    @Test
    void shouldReturnBadRequest_whenNoActiveArc() {
        final var response = request(URL + "/summary", HttpMethod.GET, Void.class);

        assertionHelper.assertBadRequest(response);
    }
}