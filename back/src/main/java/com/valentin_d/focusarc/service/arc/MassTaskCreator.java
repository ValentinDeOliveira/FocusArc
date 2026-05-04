package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskRecurrenceDto;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.service.task.TaskService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class MassTaskCreator {

    private final Arc arc;
    private final UserId userId;
    private final ChapterService chapterService;
    private final TaskService taskService;
    private final Map<LocalDate, Chapter> chapterCache;

    MassTaskCreator(Arc arc, UserId userId, ChapterLoader chapterLoader,
                    ChapterService chapterService, TaskService taskService) {
        this.arc = arc;
        this.userId = userId;
        this.chapterService = chapterService;
        this.taskService = taskService;
        this.chapterCache = new HashMap<>(
                chapterLoader.findAllByArc(arc.getId())
                        .stream()
                        .collect(Collectors.toMap(Chapter::getScheduledDate, c -> c))
        );
    }

    void execute(List<TaskRecurrenceDto> tasks) {
        final var startDate = arc.getStartDate();
        final var endDate = arc.getEndDate();

        for (final var task : tasks) {
            if (task.recurrence() instanceof TaskRecurrence.Daily) {
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    createTaskForChapter(date, task);
                }
            } else if (task.recurrence() instanceof TaskRecurrence.EveryNDays nDays) {
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(nDays.n())) {
                    createTaskForChapter(date, task);
                }
            } else if (task.recurrence() instanceof TaskRecurrence.DaysOfWeek dow) {
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    if (!dow.days().contains(date.getDayOfWeek())) continue;
                    createTaskForChapter(date, task);
                }
            } else {
                throw new IllegalStateException("Unknown recurrence type: " + task.recurrence());
            }
        }
    }

    private void createTaskForChapter(LocalDate date, TaskRecurrenceDto taskRecurrenceDto) {
        final var chapter = chapterCache.computeIfAbsent(date, d -> {
            final var dto = new ChapterCreationDto(arc.getId(), taskRecurrenceDto.estimatedMinutes(), d);
            return chapterService.create(dto, userId);
        });

        // shift the day and keep the time scheduled
        final var adjustedScheduledAt = date
                //TODO: fix once user model will have a timezone
                .atTime(taskRecurrenceDto.scheduledAt().atOffset(ZoneOffset.UTC).toLocalTime())
                .toInstant(ZoneOffset.UTC);

        final var dto = new TaskCreationDto(chapter.getId(), taskRecurrenceDto.estimatedMinutes(),
                adjustedScheduledAt, taskRecurrenceDto.name(), null, taskRecurrenceDto.tagId());

        taskService.create(dto, userId);
    }
}