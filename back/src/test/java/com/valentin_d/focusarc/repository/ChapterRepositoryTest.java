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
    private static final ArcId ARC_ID = ArcId.random();

    @BeforeEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllChapter_whenArcExists() {
        final var chapter1 = aChapterWithArcId(ARC_ID);
        final var chapter2 = aChapterWithArcId(ARC_ID);
        repository.save(chapter1);
        repository.save(chapter2);

        final var arcsLists = repository.findAllByArc(ARC_ID);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(chapter1, chapter2);
    }
}