package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskRecurrenceDto;
import com.valentin_d.focusarc.fixtures.factory.ChapterFactory;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.service.task.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerAndStartAndEndDates;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskRecurrenceDto;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskRecurrenceDtoWithScheduledAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MassTaskCreatorTest {

    @Mock private ChapterLoader chapterLoader;
    @Mock private ChapterService chapterService;
    @Mock private TaskService taskService;

    // Monday → Friday (5 days)
    private static final LocalDate START = LocalDate.of(2026, 5, 4);
    private static final LocalDate END   = LocalDate.of(2026, 5, 8);

    @Test
    void shouldCreateTaskForEachDay_whenRecurrenceIsDaily() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, END);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());

        givenChapterForAnyDate(arc, chapter);

        execute(arc, userId, List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())));

        // May 4, 5, 6, 7, 8 = 5 tasks
        verify(taskService, times(5)).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldCreateTaskEveryNDays_whenRecurrenceIsEveryNDays() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, END);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());

        givenChapterForAnyDate(arc, chapter);

        execute(arc, userId, List.of(aTaskRecurrenceDto(new TaskRecurrence.EveryNDays(2))));

        // May 4, 6, 8 = 3 tasks
        verify(taskService, times(3)).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldCreateTaskOnlyOnMatchingDays_whenRecurrenceIsDaysOfWeek() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, END);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());
        final var recurrence = new TaskRecurrence.DaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));

        givenChapterForAnyDate(arc, chapter);

        execute(arc, userId, List.of(aTaskRecurrenceDto(recurrence)));

        // May 4 (Mon), May 6 (Wed) = 2 tasks
        verify(taskService, times(2)).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldCreateChapter_whenChapterDoesNotExistForDate() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, START);
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());

        when(chapterLoader.findAllByArc(arc.getId())).thenReturn(List.of());
        when(chapterService.create(any(), eq(userId))).thenReturn(chapter);

        execute(arc, userId, List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())));

        verify(chapterService).create(any(), eq(userId));
        verify(taskService).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldReuseExistingChapter_whenChapterAlreadyExistsForDate() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, START);
        final var chapter = ChapterFactory.aChapterWithScheduledDateAndArcId(START, arc.getId());

        when(chapterLoader.findAllByArc(arc.getId())).thenReturn(List.of(chapter));

        execute(arc, userId, List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())));

        verify(chapterService, never()).create(any(), any());
        verify(taskService).create(any(TaskCreationDto.class), eq(userId));
    }

    @Test
    void shouldAdjustScheduledAtToMatchIterationDate_whenRecurrenceIsDaily() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerAndStartAndEndDates(userId, START, START.plusDays(1));
        final var chapter = ChapterFactory.aChapterWithArcId(arc.getId());
        final var baseScheduledAt = Instant.parse("2026-05-04T09:00:00Z");

        givenChapterForAnyDate(arc, chapter);

        execute(arc, userId, List.of(aTaskRecurrenceDtoWithScheduledAt(new TaskRecurrence.Daily(), baseScheduledAt)));

        final var captor = ArgumentCaptor.forClass(TaskCreationDto.class);
        verify(taskService, times(2)).create(captor.capture(), eq(userId));

        assertThat(captor.getAllValues())
                .extracting(TaskCreationDto::scheduledAt)
                .containsExactly(
                        Instant.parse("2026-05-04T09:00:00Z"),
                        Instant.parse("2026-05-05T09:00:00Z")
                );
    }

    private void execute(Arc arc, UserId userId, List<TaskRecurrenceDto> tasks) {
        new MassTaskCreator(arc, userId, chapterLoader, chapterService, taskService).execute(tasks);
    }

    private void givenChapterForAnyDate(Arc arc, Chapter chapter) {
        when(chapterLoader.findAllByArc(arc.getId())).thenReturn(List.of());
        when(chapterService.create(any(), any())).thenReturn(chapter);
    }
}