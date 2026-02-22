package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.ChapterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseChapterControllerIntegrationTest extends BaseIntegrationTest{
    @Autowired
    protected ChapterRepository chapterRepository;
    @Autowired
    protected ArcRepository arcRepository;
    protected final String URL = "/chapters";

    @BeforeEach
    public void setUp() {
        arcRepository.deleteAll();
        chapterRepository.deleteAll();
    }

    protected void assertChaptersEquals(final Chapter expected, final Chapter actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCompletedMinutes(), actual.getCompletedMinutes());
        assertEquals(expected.getPlannedMinutes(), actual.getPlannedMinutes());
        assertEquals(expected.getArc(), actual.getArc());
    }

    protected Arc createArc() {
        final var arc = anArc();
        return arcRepository.save(arc);
    }

    protected Chapter createChapter() {
        final var arc = createArc();
        return createChapterForArc(arc.getId());
    }

    protected Chapter createChapterForArc(final ArcId arcId) {
        final var chapter = aChapterWithArcId(arcId);
        return chapterRepository.save(chapter);
    }
}