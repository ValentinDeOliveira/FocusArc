package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.fixtures.task.TaskBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskCompleteDtoBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskUpdateDtoBuilder;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;

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

    public static Task aTaskWithEstimatedAndCompletedMinutes(final int estimated, final int completed) {
        return TaskBuilder.builder().estimatedMinutes(estimated).completedMinutes(completed).build().build();
    }

    public static TaskCreationDto aTaskCreationDto() {
        return TaskCreationDtoBuilder.builder().build().build();
    }

    public static TaskCreationDto aTaskCreationDtoWithChapterId(final ChapterId chapterId) {
        return TaskCreationDtoBuilder.builder().chapterId(chapterId).build().build();
    }

    public static TaskUpdateDto aTaskUpdateDto() {
        return TaskUpdateDtoBuilder.builder().build().build();
    }

    public static TaskUpdateDto aTaskUpdateDtoWithEstimatedMinutes(final int estimatedMinutes) {
        return TaskUpdateDtoBuilder.builder().estimatedMinutes(estimatedMinutes).build().build();
    }

    public static TaskCompleteDto aTaskCompleteDto() {
        return TaskCompleteDtoBuilder.builder().build().build();
    }

    public static TaskCompleteDto aTaskCompleteDtoWithMinutes(final int minutes) {
        return TaskCompleteDtoBuilder.builder().completedMinutes(minutes).build().build();
    }
}