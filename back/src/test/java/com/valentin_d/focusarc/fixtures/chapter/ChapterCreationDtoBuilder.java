package com.valentin_d.focusarc.fixtures.chapter;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.model.id.ArcId;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class ChapterCreationDtoBuilder {
    @Builder.Default
    private final ArcId arcId = ArcId.random();
    @Builder.Default
    private final int estimatedMinutes = 120;
    @Builder.Default
    private final LocalDate scheduledDate = LocalDate.now().plusDays(5);

    public ChapterCreationDto build() {
        return new ChapterCreationDto(arcId, estimatedMinutes, scheduledDate);
    }
}