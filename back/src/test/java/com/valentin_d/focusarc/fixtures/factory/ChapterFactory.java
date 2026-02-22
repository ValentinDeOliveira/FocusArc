package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.fixtures.chapter.ChapterBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterUpdateDtoBuilder;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;

public final class ChapterFactory {
    private ChapterFactory() {}

    public static Chapter aChapter() {
        return ChapterBuilder.builder().build().build();
    }

    public static Chapter aChapterWithArcId(final ArcId arcId) {
        return ChapterBuilder.builder().arc(arcId).build().build();
    }

    public static ChapterCreationDto aChapterCreationDto() {
        return ChapterCreationDtoBuilder.builder().build().build();
    }

    public static ChapterCreationDto aChapterCreationDtoWithArcId(final ArcId arcId) {
        return ChapterCreationDtoBuilder.builder().arcId(arcId).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDto() {
        return ChapterUpdateDtoBuilder.builder().build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithCompletedMinutes(final int completedMinutes) {
        return ChapterUpdateDtoBuilder.builder().completedMinutes(completedMinutes).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithPlannedMinutes(final int plannedMinutes) {
        return ChapterUpdateDtoBuilder.builder().plannedMinutes(plannedMinutes).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithCompletedMinutesAndPlannedMinutes(final int completedMinutes, final int plannedMinutes) {
        return ChapterUpdateDtoBuilder.builder().completedMinutes(completedMinutes).plannedMinutes(plannedMinutes).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithNullFields() {
        return ChapterUpdateDtoBuilder.builder().plannedMinutes(null).completedMinutes(null).build().build();
    }
}