package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.ChapterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterId;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class TaskRepositoryTest {
    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllChapter_whenArcExists() {
        final var chapterId = ChapterId.random();

        final var task1 = aTaskWithChapterId(chapterId);
        final var task2 = aTaskWithChapterId(chapterId);
        repository.save(task1);
        repository.save(task2);

        final var arcsLists = repository.findAllByChapter(chapterId);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(task1, task2);
    }
}