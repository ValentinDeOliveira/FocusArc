package com.valentin_d.focusarc.fixtures.chapter;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import lombok.Builder;

@Builder
public class ChapterBuilder {
    @Builder.Default
    private final ChapterId id = ChapterId.random();
    @Builder.Default
    private final ArcId arc = ArcId.random();
    @Builder.Default
    private final int plannedMinutes = 220;
    @Builder.Default
    private final int completedMinutes = 130;

    public Chapter build() {
        return new Chapter(id, arc, plannedMinutes, completedMinutes);
    }
}