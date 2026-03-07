package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.exception.ChapterAlreadyExistsException;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.NoChapterForArcException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChapterLoader extends BaseService {
    private final ChapterRepository chapterRepository;

    public Optional<Chapter> getChapter(ChapterId chapterId) {
        return chapterRepository.findById(chapterId);
    }

    public Chapter getChapterIfExists(ChapterId chapterId) {
        return fetchOrThrow(chapterRepository, chapterId, () -> new ChapterDoesNotExistException(chapterId));
    }

    public void assertNotAlreadyExists(ArcId arcId, LocalDate scheduledDate) {
        if (chapterRepository.existsByArcAndScheduledDate(arcId, scheduledDate)) {
            throw new ChapterAlreadyExistsException(arcId, scheduledDate);
        }
    }

    public Chapter findByDate(ArcId arcId, LocalDate scheduledDate) {
        return chapterRepository.findByArcAndScheduledDate(arcId, scheduledDate)
                .orElseThrow(() -> new NoChapterForArcException(arcId, scheduledDate));
    }
}