package com.valentin_d.focusarc.fixtures.chapter;

import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import lombok.Builder;

@Builder
public class ChapterUpdateDtoBuilder {
    @Builder.Default
    private final Integer completedMinutes = 100;
    @Builder.Default
    private final Integer plannedMinutes = 180;

    public ChapterUpdateDto build() {
        return new ChapterUpdateDto(completedMinutes, plannedMinutes);
    }
}