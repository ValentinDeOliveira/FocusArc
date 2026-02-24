package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.ArcService;
import com.valentin_d.focusarc.service.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterId;

public class BaseRecalculationIntegrationTest extends BaseTaskControllerIntegrationTest {
    @Autowired
    protected ChapterService chapterService;
    @Autowired
    protected ArcRepository arcRepository;
    @Autowired
    protected ArcService arcService;
    protected final String URL = "/tasks";

    @BeforeEach
    public void setUp() {
        arcRepository.deleteAll();
    }

    public Task createTaskForArc(){
        final var arc = anArc();
        final var chapter = aChapterWithArcId(arc.getId());
        final var task = aTaskWithChapterId(chapter.getId());

        arcRepository.save(arc);
        chapterRepository.save(chapter);
        return taskRepository.save(task);
    }
}