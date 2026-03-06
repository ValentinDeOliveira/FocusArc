package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.user.UserLoader;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ContextLoader extends BaseService {
    private final UserLoader userLoader;
    private final ArcLoader arcLoader;
    private final ChapterLoader chapterLoader;

    public Chapter getChapterFromUserId(@NotNull final UserId userId) {
        userLoader.assertUserExists(userId);
        final var arc = arcLoader.getActiveArcForUser(userId);
        return chapterLoader.findByDate(arc.getId(), LocalDate.now());
    }

    public void assertChapterForUser(@NotNull final ChapterId chapterId, @NotNull final UserId userId) {
        final var arcId = chapterLoader.getChapterIfExists(chapterId).getArc();
        arcLoader.assertArcExistsForUser(arcId, userId);
    }
}