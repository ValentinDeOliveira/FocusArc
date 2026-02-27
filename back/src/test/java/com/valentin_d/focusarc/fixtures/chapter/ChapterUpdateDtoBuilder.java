package com.valentin_d.focusarc.fixtures.chapter;

import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class ChapterUpdateDtoBuilder {
    @Builder.Default
    private final LocalDate scheduledDate = LocalDate.now().plusDays(1);

    public ChapterUpdateDto build() {
        return new ChapterUpdateDto(scheduledDate);
    }
}