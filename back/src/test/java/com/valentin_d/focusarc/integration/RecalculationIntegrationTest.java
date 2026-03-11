package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseRecalculationIntegrationTest;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskCompleteDto;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskCreationDtoWithChapterIdAndScheduled;
import static org.junit.jupiter.api.Assertions.assertEquals;

//TODO: not sure about inheritence
public class RecalculationIntegrationTest extends BaseRecalculationIntegrationTest {
    @Test
    public void shouldCreateTaskAndRecalculateEstimatedMinutes_whenDataIsValid() {
        final var task = domainFixture.taskForUser(user.getId());

        final var dto = aTaskCreationDtoWithChapterIdAndScheduled(task.getChapter(),
                task.getStartAt().plus(1, ChronoUnit.DAYS));
        request(URL, HttpMethod.POST, dto, Void.class);

        final var totalEstimatedMinutes = task.getEstimatedMinutes() + dto.estimatedMinutes();

        assertCorrectRecalculation(task, totalEstimatedMinutes, Chapter::getEstimatedMinutes,
                Arc::getTotalEstimatedMinutes);
    }

    @Test
    public void shouldUpdateTaskAndRecalculateEstimatedMinutes_whenDataChangedEstimatedMinutes() {
        final var task = domainFixture.taskForUser(user.getId());

        final var dto = TaskUpdateDto.builder().estimatedMinutes(50).build();
        request(tasksUrl(task.getId()), HttpMethod.PUT, dto, Void.class);

        assertCorrectRecalculation(task, dto.estimatedMinutes(), Chapter::getEstimatedMinutes,
                Arc::getTotalEstimatedMinutes);
    }

    @Test
    public void shouldUpdateTaskAndRecalculateCompletedMinutes_whenDataChangedCompletedMinutes() {
        final var task = domainFixture.taskForUser(user.getId());

        final var dto = TaskUpdateDto.builder().completedMinutes(50).build();
        request(tasksUrl(task.getId()), HttpMethod.PUT, dto, Void.class);

        assertCorrectRecalculation(task, dto.completedMinutes(), Chapter::getCompletedMinutes,
                Arc::getTotalCompletedMinutes);
    }

    @Test
    public void shouldRecalculateCompletedMinutes_whenTaskIsCompleted() {
        final var task = domainFixture.taskForUser(user.getId());

        final var dto = aTaskCompleteDto();
        request(tasksUrl(task.getId()) + "/complete" , HttpMethod.PATCH,
                dto, Void.class);

        final var savedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals(savedTask.getCompletedMinutes(), dto.completedMinutes());
        assertEquals(TaskStatus.DONE, savedTask.getStatus());

        assertCorrectRecalculation(task, dto.completedMinutes(), Chapter::getCompletedMinutes,
                Arc::getTotalCompletedMinutes);
    }

    @Test
    public void shouldDeleteTaskAndRecalculateEstimatedMinutes_whenTaskIsDeleted() {
        final var task1 = domainFixture.taskForUser(user.getId());
        final var task2 = domainFixture.taskForChapter(task1.getChapter());

        request(tasksUrl(task1.getId()), HttpMethod.DELETE, Void.class);

        assertCorrectRecalculation(task2, task2.getEstimatedMinutes(), Chapter::getEstimatedMinutes,
                Arc::getTotalEstimatedMinutes);
    }

    private void assertCorrectRecalculation(final Task task, final int expectedValue,
                               final Function<Chapter, Integer> chapterField,
                               final Function<Arc, Integer> arcField) {
        final var savedChapter = chapterRepository.findById(task.getChapter()).orElseThrow();
        final var savedArc = arcRepository.findById(savedChapter.getArc()).orElseThrow();

        assertEquals(expectedValue, chapterField.apply(savedChapter));
        assertEquals(expectedValue, arcField.apply(savedArc));
        assertEquals(chapterField.apply(savedChapter), arcField.apply(savedArc));
    }
}