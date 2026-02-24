package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.integration.base.BaseRecalculationIntegrationTest;
import com.valentin_d.focusarc.model.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskCreationDtoWithChapterId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterId;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecalculationIntegrationTest extends BaseRecalculationIntegrationTest {
    @Test
    public void shouldCreateTaskAndRecalculateEstimatedMinutes_whenDataIsValid() {
        final var arc = anArc();
        final var chapter = aChapterWithArcId(arc.getId());
        final var task = aTaskWithChapterId(chapter.getId());

        arcRepository.save(arc);
        chapterRepository.save(chapter);
        taskRepository.save(task);

        final var dto = aTaskCreationDtoWithChapterId(chapter.getId());
        request(URL, HttpMethod.POST, dto, Task.class);

        final var savedChapter = chapterRepository.findById(chapter.getId()).orElseThrow();
        final var savedArc = arcRepository.findById(arc.getId()).orElseThrow();

        final var totalEstimatedMinutes = task.getEstimatedMinutes() + dto.estimatedMinutes();

        assertEquals(totalEstimatedMinutes, savedChapter.getEstimatedMinutes());
        assertEquals(totalEstimatedMinutes, savedArc.getTotalEstimatedMinutes());
        assertEquals(savedChapter.getEstimatedMinutes(), savedArc.getTotalEstimatedMinutes());
    }
}