package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.exception.chapter.ChapterAlreadyExistsException;
import com.valentin_d.focusarc.exception.chapter.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.chapter.NoChapterForArcException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.service.BaseLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChapterLoader extends BaseLoader {
    private final ChapterRepository chapterRepository;

    public Chapter getChapterIfExists(ChapterId chapterId) {
        return fetchOrThrow(chapterRepository, chapterId, () -> new ChapterDoesNotExistException(chapterId));
    }

    public Optional<Chapter> findChapterByScheduledDate(ArcId arcId, LocalDate scheduledDate) {
        return chapterRepository.findByArcAndScheduledDate(arcId, scheduledDate);
    }

    public void assertNotAlreadyExists(ArcId arcId, LocalDate scheduledDate) {
        if (chapterRepository.existsByArcAndScheduledDate(arcId, scheduledDate)) {
            throw new ChapterAlreadyExistsException(arcId, scheduledDate);
        }
    }

    public Chapter getChapterByDate(ArcId arcId, LocalDate scheduledDate) {
        return findChapterByScheduledDate(arcId, scheduledDate)
                .orElseThrow(() -> new NoChapterForArcException(arcId, scheduledDate));
    }

    public List<Chapter> findAllByArc(ArcId arcId) {
        return chapterRepository.findAllByArc(arcId);
    }
}