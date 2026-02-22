package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.fixtures.task.TaskBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.task.TaskUpdateDtoBuilder;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;

public final class TaskFactory {
    private TaskFactory() {}

    public static Task aTask() {
        return TaskBuilder.builder().build().build();
    }

    public static Task aTaskWithChapterId(final ChapterId chapterId) {
        return TaskBuilder.builder().chapter(chapterId).build().build();
    }

    public static TaskCreationDto aTaskCreationDto() {
        return TaskCreationDtoBuilder.builder().build().build();
    }

    public static TaskUpdateDto aTaskUpdateDto() {
        return TaskUpdateDtoBuilder.builder().build().build();
    }
}