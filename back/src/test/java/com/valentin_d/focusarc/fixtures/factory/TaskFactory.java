package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.fixtures.task.TaskBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskCompleteDtoBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskUpdateDtoBuilder;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;

import java.time.Instant;

public final class TaskFactory {
    private TaskFactory() {}

    public static Task aTask() {
        return TaskBuilder.builder().build().build();
    }

    public static Task aTaskWithChapterId(final ChapterId chapterId) {
        return TaskBuilder.builder().chapter(chapterId).build().build();
    }

    public static Task aTaskWithStatus(final TaskStatus status) {
        return TaskBuilder.builder().taskStatus(status).build().build();
    }

    public static Task aTaskWithChapterIdAndStatus(final ChapterId chapterId, final TaskStatus status) {
        return TaskBuilder.builder().chapter(chapterId).taskStatus(status).build().build();
    }

    public static Task aTaskWithChapterIdAndTag(final ChapterId chapterId, final TagId tagId) {
        return TaskBuilder.builder().chapter(chapterId).tag(tagId).build().build();
    }

    public static Task aTaskWithChapterIdAndStatusAndTag(final ChapterId chapterId, final TaskStatus status, final TagId tagId) {
        return TaskBuilder.builder().chapter(chapterId).taskStatus(status).tag(tagId).build().build();
    }

    public static Task aTaskWithEstimatedAndCompletedMinutes(final int estimated, final int completed) {
        return TaskBuilder.builder().estimatedMinutes(estimated).completedMinutes(completed).build().build();
    }

    public static TaskCreationDto aTaskCreationDto() {
        return TaskCreationDtoBuilder.builder().build().build();
    }

    public static TaskCreationDto aTaskCreationDtoWithChapterIdWithTag(final ChapterId chapterId, final TagId tagId) {
        return TaskCreationDtoBuilder.builder().chapterId(chapterId).tagId(tagId).build().build();
    }

    public static TaskCreationDto aTaskCreationDtoWithChapterIdAndEstimatedMinutes(final ChapterId chapterId,
                                                                                   final int estimated) {
        return TaskCreationDtoBuilder.builder().chapterId(chapterId).estimatedMinutes(estimated).build().build();
    }

    public static TaskCreationDto aTaskCreationDtoWithChapterIdAndScheduled(final ChapterId chapterId,
                                                                            final Instant scheduled) {
        return TaskCreationDtoBuilder.builder().chapterId(chapterId).scheduledAt(scheduled).tagId(null).build().build();
    }

    public static TaskUpdateDto aTaskUpdateDto() {
        return TaskUpdateDtoBuilder.builder().build().build();
    }

    public static Task aTaskWithChapterIdAndWindow(final ChapterId chapterId, final Instant startAt,
                                                   final int estimatedMinutes) {
        return new Task(chapterId, estimatedMinutes, startAt, "task");
    }

    public static TaskCreationDto aTaskCreationDtoWithChapterIdAndWindow(final ChapterId chapterId,
                                                                         final Instant scheduledAt,
                                                                         final int estimatedMinutes) {
        return TaskCreationDtoBuilder.builder()
                .chapterId(chapterId).scheduledAt(scheduledAt).estimatedMinutes(estimatedMinutes).tagId(null)
                .build().build();
    }

    public static TaskCompleteDto aTaskCompleteDto() {
        return TaskCompleteDtoBuilder.builder().build().build();
    }

    public static TaskCompleteDto aTaskCompleteDtoWithMinutes(final int minutes) {
        return TaskCompleteDtoBuilder.builder().completedMinutes(minutes).build().build();
    }
}