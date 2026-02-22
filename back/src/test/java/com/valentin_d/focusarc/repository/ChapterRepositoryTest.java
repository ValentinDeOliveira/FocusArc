package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.ArcId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class ChapterRepositoryTest {
    @Autowired
    private ChapterRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllChapter_whenArcExists() {
        final var arcId = ArcId.random();

        final var chapter1 = aChapterWithArcId(arcId);
        final var chapter2 = aChapterWithArcId(arcId);
        repository.save(chapter1);
        repository.save(chapter2);

        final var arcsLists = repository.findAllByArc(arcId);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(chapter1, chapter2);
    }
}