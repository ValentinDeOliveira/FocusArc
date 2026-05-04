package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.user.UserLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContextLoader extends BaseService {
    private final UserLoader userLoader;
    private final ArcLoader arcLoader;
    private final ChapterLoader chapterLoader;

    public Chapter getChapterFromUserId(UserId userId) {
        userLoader.assertUserExists(userId);
        final var arc = arcLoader.getActiveArcForUser(userId);
        return chapterLoader.findByDate(arc.getId(), LocalDate.now());
    }

    public void assertChapterForUser(ChapterId chapterId, UserId userId) {
        final var arcId = chapterLoader.getChapterIfExists(chapterId).getArc();
        arcLoader.assertArcExistsForUser(arcId, userId);
    }

    public Optional<Chapter> getChapterIfExistsForUser(ArcId arcId, LocalDate scheduledDate, UserId userId) {
        var chapter = chapterLoader.getChapterByScheduledDate(arcId, scheduledDate);

        if (chapter.isEmpty()) {
            return Optional.empty();
        }

        arcLoader.assertArcExistsForUser(chapter.get().getArc(),  userId);
        return chapter;
    }
}