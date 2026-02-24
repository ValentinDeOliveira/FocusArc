package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseRecalculationIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskCreationDtoWithChapterId;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecalculationIntegrationTest extends BaseRecalculationIntegrationTest {
    @Test
    public void shouldCreateTaskAndRecalculateEstimatedMinutes_whenDataIsValid() {
        final var task = createTaskForArc();

        final var dto = aTaskCreationDtoWithChapterId(task.getChapter());
        request(URL, HttpMethod.POST, dto, Void.class);

        final var savedChapter = chapterRepository.findById(task.getChapter()).orElseThrow();
        final var savedArc = arcRepository.findById(savedChapter.getArc()).orElseThrow();

        final var totalEstimatedMinutes = task.getEstimatedMinutes() + dto.estimatedMinutes();

        assertEquals(totalEstimatedMinutes, savedChapter.getEstimatedMinutes());
        assertEquals(totalEstimatedMinutes, savedArc.getTotalEstimatedMinutes());
        assertEquals(savedChapter.getEstimatedMinutes(), savedArc.getTotalEstimatedMinutes());
    }

    @Test
    public void shouldUpdateTaskAndRecalculateEstimatedMinutes_whenDataChangedEstimatedMinutes() {
        final var task = createTaskForArc();

        final var dto = TaskUpdateDto.builder().estimatedMinutes(50).build();
        request(URL  + "/" + task.getId().id(), HttpMethod.PUT, dto, Void.class);

        final var savedChapter = chapterRepository.findById(task.getChapter()).orElseThrow();
        final var savedArc = arcRepository.findById(savedChapter.getArc()).orElseThrow();

        assertEquals(dto.estimatedMinutes(), savedChapter.getEstimatedMinutes());
        assertEquals(dto.estimatedMinutes(), savedArc.getTotalEstimatedMinutes());
        assertEquals(savedChapter.getEstimatedMinutes(), savedArc.getTotalEstimatedMinutes());
    }

    @Test
    public void shouldUpdateTaskAndRecalculateCompletedMinutes_whenDataChangedCompletedMinutes() {
        final var task = createTaskForArc();

        final var dto = TaskUpdateDto.builder().completedMinutes(50).build();
        request(URL  + "/" + task.getId().id(), HttpMethod.PUT, dto, Void.class);

        final var savedChapter = chapterRepository.findById(task.getChapter()).orElseThrow();
        final var savedArc = arcRepository.findById(savedChapter.getArc()).orElseThrow();

        assertEquals(dto.completedMinutes(), savedChapter.getCompletedMinutes());
        assertEquals(dto.completedMinutes(), savedArc.getTotalCompletedMinutes());
        assertEquals(savedChapter.getCompletedMinutes(), savedArc.getTotalCompletedMinutes());
    }
}