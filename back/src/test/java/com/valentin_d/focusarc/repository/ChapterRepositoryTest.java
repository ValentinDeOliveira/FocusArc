package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.ArcId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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

        final var now = LocalDate.now();
        final var chapter1 = aChapterWithScheduledDateAndArcId(now.plusDays(5), arcId);
        final var chapter2 = aChapterWithScheduledDateAndArcId(now.plusDays(9), arcId);
        repository.save(chapter1);
        repository.save(chapter2);

        final var arcsLists = repository.findAllByArc(arcId);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(chapter1, chapter2);
    }

    @Test
    void shouldRejectDuplicate_whenArcExistsAtDate() {
        final var date = LocalDate.now();
        final var arcId = ArcId.random();

        final var first = aChapterWithScheduledDateAndArcId(date, arcId);
        final var duplicate = aChapterWithScheduledDateAndArcId(date, arcId);

        repository.save(first);
        assertThatThrownBy(() -> repository.save(duplicate))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void shouldReturnTrue_whenChapterExistsAtDate() {
        final var chapter = repository.save(aChapter());

        assertTrue(repository.existsByArcAndScheduledDate(chapter.getArc(), chapter.getScheduledDate()));
    }

    @Test
    void shouldReturnFalse_whenChapterDoesNotExistsAtDate() {
        final var now = LocalDate.now();
        final var chapter = repository.save(aChapterWithScheduledDate(now.plusDays(2)));

        assertFalse(repository.existsByArcAndScheduledDate(chapter.getArc(), now.plusDays(4)));
    }
}