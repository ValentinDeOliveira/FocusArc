package com.valentin_d.focusarc.fixtures.chapter;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class ChapterBuilder {
    @Builder.Default
    private final ChapterId id = ChapterId.random();
    @Builder.Default
    private final ArcId arc = ArcId.random();
    @Builder.Default
    private final int estimatedMinutes = 220;
    @Builder.Default
    private final int completedMinutes = 130;
    @Builder.Default
    private final LocalDate scheduledDate = LocalDate.now();

    public Chapter build() {
        return new Chapter(id, arc, estimatedMinutes, completedMinutes, scheduledDate);
    }
}