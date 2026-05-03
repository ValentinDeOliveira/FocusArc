package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.dto.tag.TagTaskStatsDto;
import com.valentin_d.focusarc.dto.task.TaskStatsDto;
import com.valentin_d.focusarc.integration.base.BaseArcControllerIntegrationTest;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import com.valentin_d.focusarc.model.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskRecurrenceDto;
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
        domainFixture.chapterForArcWithDateAllTaskDone(arc.getId(), LocalDate.now().minusDays(1)); // COMPLETED
        domainFixture.chapterForArcWithDateAllTaskDone(arc.getId(), LocalDate.now().minusDays(2)); // COMPLETED
        domainFixture.plannedChapterForArcWithDate(arc.getId(), LocalDate.now().plusDays(1)); // PLANNED

        final var response = request(URL + "/summary", HttpMethod.GET, ArcSummaryResponseDto.class);

        assertionHelper.assertOk(response);
        final var summary = response.getBody();
        assertNotNull(summary);
        assertEquals(summary.arcId(), arc.getId());
        assertEquals(summary.name(), arc.getName());
        assertEquals(arc.getTotalEstimatedMinutes(), summary.totalEstimatedMinutes());
        assertEquals(2, summary.nbChapterCompleted());
        assertEquals(1, summary.nbChapterPlanned());
        assertEquals(0, summary.nbChapterSkipped());
        assertEquals(2, summary.daysStreak());
    }

    @Test
    void shouldReturnBadRequestOnSummary_whenNoActiveArc() {
        final var response = request(URL + "/summary", HttpMethod.GET, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnStats_whenUserHasActiveArc() {
        final var arc = domainFixture.arcForUser(user.getId());
        final var chapter1 = domainFixture.chapterForArc(arc.getId());
        final var chapter2 = domainFixture.chapterForArc(arc.getId());

        final var tag1 = domainFixture.tagForUser(user.getId());
        final var tag2 = domainFixture.tagForUser(user.getId());

        // tag1: 3 tasks, 2 done
        domainFixture.taskForChapterWithStatusAndTag(chapter1.getId(), TaskStatus.DONE, tag1.getId());
        domainFixture.taskForChapterWithStatusAndTag(chapter1.getId(), TaskStatus.DONE, tag1.getId());
        domainFixture.taskForChapterWithTag(chapter2.getId(), tag1.getId());

        // tag2: 1 task, 0 done
        domainFixture.taskForChapterWithTag(chapter2.getId(), tag2.getId());

        final var response = request(URL + "/tag-stats", HttpMethod.GET, TagTaskStatsDto[].class);

        assertionHelper.assertOk(response);
        final var stats = response.getBody();
        assertNotNull(stats);
        assertThat(stats).hasSize(2);

        final var statForTag1 = Arrays.stream(stats)
                .filter(s -> s.tagId().equals(tag1.getId())).findFirst().orElseThrow();
        final var statForTag2 = Arrays.stream(stats)
                .filter(s -> s.tagId().equals(tag2.getId())).findFirst().orElseThrow();

        assertEquals(3L, statForTag1.total());
        assertEquals(2L, statForTag1.done());
        assertEquals(1L, statForTag2.total());
        assertEquals(0L, statForTag2.done());
    }

    @Test
    void shouldReturnBadRequestOnStats_whenNoActiveArc() {
        final var response = request(URL + "/tag-stats", HttpMethod.GET, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnTaskStats_whenUserHasActiveArc() {
        final var arc = domainFixture.arcForUser(user.getId());
        final var chapter1 = domainFixture.chapterForArc(arc.getId());
        final var chapter2 = domainFixture.chapterForArc(arc.getId());

        // 2 DONE tasks across two chapters
        domainFixture.taskForChapterWithStatus(chapter1.getId(), TaskStatus.DONE);
        domainFixture.taskForChapterWithStatus(chapter2.getId(), TaskStatus.DONE);
        // 1 PLANNED task
        domainFixture.taskForChapterWithStatus(chapter1.getId(), TaskStatus.PLANNED);

        final var response = request(URL + "/task-stats", HttpMethod.GET, TaskStatsDto[].class);

        assertionHelper.assertOk(response);
        final var stats = response.getBody();
        assertNotNull(stats);
        assertThat(stats).hasSize(2);

        final var statForDone = Arrays.stream(stats)
                .filter(s -> s.taskStatus() == TaskStatus.DONE).findFirst().orElseThrow();
        final var statForPlanned = Arrays.stream(stats)
                .filter(s -> s.taskStatus() == TaskStatus.PLANNED).findFirst().orElseThrow();

        assertEquals(3L, statForDone.total());
        assertEquals(2L, statForDone.done());
        assertEquals(3L, statForPlanned.total());
        assertEquals(1L, statForPlanned.done());
    }

    @Test
    void shouldReturnBadRequestOnTaskStats_whenNoActiveArc() {
        final var response = request(URL + "/task-stats", HttpMethod.GET, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldCreateChaptersAndTasksForEachDay_whenRecurrenceIsDaily() {
        final var start = LocalDate.now().plusDays(1);
        final var end = start.plusDays(2); // 3 days total
        final var arc = domainFixture.arcForUserWithDates(user.getId(), start, end);

        final var response = request(massCreateUrl(arc.getId()), HttpMethod.POST,
                List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), Void.class);

        assertionHelper.assertNoContent(response);

        final var chapters = request(chaptersForArcUrl(arc.getId()), HttpMethod.GET, Chapter[].class);
        assertThat(chapters.getBody()).hasSize(3);

        final var firstChapterId = chapters.getBody()[0].getId();
        final var tasks = request(tasksForChapterUrl(firstChapterId), HttpMethod.GET, Task[].class);
        assertThat(tasks.getBody()).hasSize(1);
    }

    @Test
    void shouldReuseExistingChapter_whenChapterAlreadyExistsForDate_onMassCreate() {
        final var start = LocalDate.now().plusDays(1);
        final var arc = domainFixture.arcForUserWithDates(user.getId(), start, start);
        domainFixture.chapterForArcWithDate(arc.getId(), start);

        final var response = request(massCreateUrl(arc.getId()), HttpMethod.POST,
                List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), Void.class);

        assertionHelper.assertNoContent(response);

        final var chapters = request(chaptersForArcUrl(arc.getId()), HttpMethod.GET, Chapter[].class);
        assertThat(chapters.getBody()).hasSize(1);
    }

    @Test
    void shouldCreateTasksOnlyOnMatchingDays_whenRecurrenceIsDaysOfWeek() {
        final var monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        final var friday = monday.plusDays(4);
        final var arc = domainFixture.arcForUserWithDates(user.getId(), monday, friday);
        final var recurrence = new TaskRecurrence.DaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));

        final var response = request(massCreateUrl(arc.getId()), HttpMethod.POST,
                List.of(aTaskRecurrenceDto(recurrence)), Void.class);

        assertionHelper.assertNoContent(response);

        final var chapters = request(chaptersForArcUrl(arc.getId()), HttpMethod.GET, Chapter[].class);
        assertThat(chapters.getBody()).hasSize(2); // only Mon and Wed
    }

    @Test
    void shouldReturnNotFound_whenArcDoesNotExist_onMassCreate() {
        final var response = request(massCreateUrl(ArcId.random()), HttpMethod.POST,
                List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), Void.class);

        assertionHelper.assertNotFound(response);
    }
}