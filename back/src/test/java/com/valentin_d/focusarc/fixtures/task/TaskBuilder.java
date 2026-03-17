package com.valentin_d.focusarc.fixtures.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
public class TaskBuilder {
    @Builder.Default
    private final TaskId id = TaskId.random();
    @Builder.Default
    private final ChapterId chapter = ChapterId.random();
    @Builder.Default
    private final int estimatedMinutes = 220;
    @Builder.Default
    private final int completedMinutes = 130;
    @Builder.Default
    private final Instant startAt = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(120);
    @Builder.Default
    private final Instant endAt = Instant.now().truncatedTo(ChronoUnit.MILLIS).plus(1, ChronoUnit.HOURS);
    @Builder.Default
    private final TaskStatus taskStatus = TaskStatus.PLANNED;
    @Builder.Default
    private final String name = "My task";
    @Builder.Default
    private final String description = "My description";
    @Builder.Default
    private final TagId tag = TagId.random();

    public Task build() {
        return new Task(id, chapter, estimatedMinutes, completedMinutes, startAt, startAt, endAt, endAt,
                taskStatus, name, description, tag);
    }
}