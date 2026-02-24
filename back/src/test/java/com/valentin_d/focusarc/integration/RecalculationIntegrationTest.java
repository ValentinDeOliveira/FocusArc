package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseRecalculationIntegrationTest;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.function.Function;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskCompleteDto;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskCreationDtoWithChapterId;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecalculationIntegrationTest extends BaseRecalculationIntegrationTest {
    @Test
    public void shouldCreateTaskAndRecalculateEstimatedMinutes_whenDataIsValid() {
        final var task = createTaskForArc();

        final var dto = aTaskCreationDtoWithChapterId(task.getChapter());
        request(URL, HttpMethod.POST, dto, Void.class);

        final var totalEstimatedMinutes = task.getEstimatedMinutes() + dto.estimatedMinutes();

        assertCorrectRecalculation(task, totalEstimatedMinutes, Chapter::getEstimatedMinutes,
                Arc::getTotalEstimatedMinutes);
    }

    @Test
    public void shouldUpdateTaskAndRecalculateEstimatedMinutes_whenDataChangedEstimatedMinutes() {
        final var task = createTaskForArc();

        final var dto = TaskUpdateDto.builder().estimatedMinutes(50).build();
        request(URL  + "/" + task.getId().id(), HttpMethod.PUT, dto, Void.class);

        assertCorrectRecalculation(task, dto.estimatedMinutes(), Chapter::getEstimatedMinutes,
                Arc::getTotalEstimatedMinutes);
    }

    @Test
    public void shouldUpdateTaskAndRecalculateCompletedMinutes_whenDataChangedCompletedMinutes() {
        final var task = createTaskForArc();

        final var dto = TaskUpdateDto.builder().completedMinutes(50).build();
        request(URL  + "/" + task.getId().id(), HttpMethod.PUT, dto, Void.class);

        assertCorrectRecalculation(task, dto.completedMinutes(), Chapter::getCompletedMinutes,
                Arc::getTotalCompletedMinutes);
    }

    @Test
    public void shouldRecalculateCompletedMinutes_whenTaskIsCompleted() {
        final var task = createTaskForArc();

        final var dto = aTaskCompleteDto();
        request(URL  + "/" + task.getId().id() + "/complete" , HttpMethod.PATCH, dto, Void.class);

        final var savedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals(savedTask.getCompletedMinutes(), dto.completedMinutes());
        assertEquals(TaskStatus.DONE, savedTask.getStatus());

        assertCorrectRecalculation(task, dto.completedMinutes(), Chapter::getCompletedMinutes,
                Arc::getTotalCompletedMinutes);
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