package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.exception.ChapterAlreadyExistsException;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.NoChapterForArcException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.service.BaseService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChapterLoader extends BaseService {
    private final ChapterRepository chapterRepository;

    public Optional<Chapter> getChapter(@NotNull final ChapterId chapterId) {
        return chapterRepository.findById(chapterId);
    }

    public Chapter getChapterIfExists(@NotNull final ChapterId chapterId) {
        return fetchOrThrow(chapterRepository, chapterId, () -> new ChapterDoesNotExistException(chapterId));
    }

    public void assertNotAlreadyExists(@NotNull final ArcId arcId, @NotNull final LocalDate scheduledDate) {
        if (chapterRepository.existsByArcAndScheduledDate(arcId, scheduledDate)) {
            throw new ChapterAlreadyExistsException(arcId, scheduledDate);
        }
    }

    public Chapter findByDate(@NotNull final ArcId arcId, @NotNull final LocalDate scheduledDate) {
        return chapterRepository.findByArcAndScheduledDate(arcId, scheduledDate)
                .orElseThrow(() -> new NoChapterForArcException(arcId, scheduledDate));
    }
}