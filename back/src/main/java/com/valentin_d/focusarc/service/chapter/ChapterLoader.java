package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.service.BaseService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChapterLoader extends BaseService {
    private final ChapterRepository chapterRepository;

    public Chapter getChapterIfExists(@NotNull final ChapterId chapterId) {
        return fetchOrThrow(chapterRepository, chapterId, () -> new ChapterDoesNotExistException(chapterId));
    }

    public void assertChapterExists(@NotNull final ChapterId chapterId) {
        existsOrThrow(chapterRepository, chapterId, () -> new ChapterDoesNotExistException(chapterId));
    }
}