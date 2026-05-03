package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.fixtures.arc.ArcBuilder;
import com.valentin_d.focusarc.fixtures.factory.ChapterFactory;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerAndStartAndEndDates;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskRecurrenceDto;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskRecurrenceDtoWithScheduledAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ArcServiceMassCreateTest extends BaseArcServiceTest {

    // Monday → Friday (5 days)
    private static final LocalDate START = LocalDate.of(2026, 5, 4);
    private static final LocalDate END   = LocalDate.of(2026, 5, 8);

    @Test
    void shouldThrowException_whenArcDoesNotExist() {
        final var userId = UserId.random();
        final var arcId = ArcId.random();

        doThrowArcDoesNotExistForUser(arcId, userId);

        assertThatThrownBy(() -> arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), arcId, userId))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(contextLoader, never()).getChapterIfExistsForUser(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("provideArcDateException")
    void shouldThrowException_whenDateIsInvalid(@NotNull final Arc arc) {
        when(arcLoader.getArcIfExistsForUser(arc.getId(), arc.getOwner())).thenReturn(arc);

        assertThatThrownBy(() -> arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), arc.getId(), arc.getOwner()))
                .isInstanceOf(IllegalStateException.class);

        verify(contextLoader, never()).getChapterIfExistsForUser(any(), any(), any());
    }

    @Test
    void shouldCreateTaskForEachDay_whenRecurrenceIsDaily() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, END);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());

        givenArcWithChapterForAnyDate(arc, chapter);

        arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), arc.getId(), userId);

        // May 4, 5, 6, 7, 8 = 5 tasks
        verify(taskService, times(5)).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldCreateTaskEveryNDays_whenRecurrenceIsEveryNDays() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, END);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());

        givenArcWithChapterForAnyDate(arc, chapter);

        arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.EveryNDays(2))), arc.getId(), userId);

        // May 4, 6, 8 = 3 tasks
        verify(taskService, times(3)).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldCreateTaskOnlyOnMatchingDays_whenRecurrenceIsDaysOfWeek() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, END);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());
        final var recurrence = new TaskRecurrence.DaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));

        givenArcWithChapterForAnyDate(arc, chapter);

        arcService.massCreate(List.of(aTaskRecurrenceDto(recurrence)), arc.getId(), userId);

        // May 4 (Mon), May 6 (Wed) = 2 tasks
        verify(taskService, times(2)).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldCreateChapter_whenChapterDoesNotExistForDate() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, START);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());

        when(arcLoader.getArcIfExistsForUser(arc.getId(), userId)).thenReturn(arc);
        when(contextLoader.getChapterIfExistsForUser(eq(arc.getId()), eq(START), eq(userId)))
                .thenReturn(Optional.empty());
        when(chapterService.create(any(), eq(userId))).thenReturn(chapter);

        arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), arc.getId(), userId);

        verify(chapterService).create(any(), eq(userId));
        verify(taskService).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldReuseExistingChapter_whenChapterAlreadyExistsForDate() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, START);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());

        when(arcLoader.getArcIfExistsForUser(arc.getId(), userId)).thenReturn(arc);
        when(contextLoader.getChapterIfExistsForUser(eq(arc.getId()), eq(START), eq(userId)))
                .thenReturn(Optional.of(chapter));

        arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), arc.getId(), userId);

        verify(chapterService, never()).create(any(), any());
        verify(taskService).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldAdjustScheduledAtToMatchIterationDate_whenRecurrenceIsDaily() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, START.plusDays(1));
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());
        final var baseScheduledAt = Instant.parse("2026-05-04T09:00:00Z");

        givenArcWithChapterForAnyDate(arc, chapter);

        arcService.massCreate(List.of(aTaskRecurrenceDtoWithScheduledAt(new TaskRecurrence.Daily(), baseScheduledAt)), arc.getId(), userId);

        final var captor = ArgumentCaptor.forClass(TaskCreationDto.class);
        verify(taskService, times(2)).create(captor.capture(), eq(userId));

        assertThat(captor.getAllValues())
                .extracting(TaskCreationDto::scheduledAt)
                .containsExactly(
                        Instant.parse("2026-05-04T09:00:00Z"),
                        Instant.parse("2026-05-05T09:00:00Z")
                );
    }

    private void givenArcWithChapterForAnyDate(final Arc arc, final Chapter chapter) {
        when(arcLoader.getArcIfExistsForUser(arc.getId(), arc.getOwner())).thenReturn(arc);
        when(contextLoader.getChapterIfExistsForUser(eq(arc.getId()), any(LocalDate.class), eq(arc.getOwner())))
                .thenReturn(Optional.of(chapter));
    }

    private static Stream<Arc> provideArcDateException() {
        return Stream.of(
                ArcBuilder.builder().owner(UserId.random()).startDate(null).build().build(),
                ArcBuilder.builder().owner(UserId.random()).endDate(null).build().build()
        );
    }
}