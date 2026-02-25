package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.fixtures.chapter.ChapterBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterUpdateDtoBuilder;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;

import java.time.LocalDate;

public final class ChapterFactory {
    private ChapterFactory() {}

    public static Chapter aChapter() {
        return ChapterBuilder.builder().build().build();
    }

    public static Chapter aChapterWithArcId(final ArcId arcId) {
        return ChapterBuilder.builder().arc(arcId).build().build();
    }

    public static Chapter aChapterWithScheduledDate(final LocalDate date) {
        return ChapterBuilder.builder().scheduledDate(date).build().build();
    }

    public static Chapter aChapterWithScheduledDateAndArcId(final LocalDate date, final ArcId arcId) {
        return ChapterBuilder.builder().scheduledDate(date).arc(arcId).build().build();
    }

    public static ChapterCreationDto aChapterCreationDto() {
        return ChapterCreationDtoBuilder.builder().build().build();
    }

    public static ChapterCreationDto aChapterCreationDtoWithArcId(final ArcId arcId) {
        return ChapterCreationDtoBuilder.builder().arcId(arcId).build().build();
    }

    public static ChapterCreationDto aChapterCreationDtoWithArcIdAndScheduledDate(final ArcId arcId, final LocalDate date) {
        return ChapterCreationDtoBuilder.builder().arcId(arcId).scheduledDate(date).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDto() {
        return ChapterUpdateDtoBuilder.builder().build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithCompletedMinutes(final int completedMinutes) {
        return ChapterUpdateDtoBuilder.builder().completedMinutes(completedMinutes).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithEstimatedMinutes(final int estimatedMinutes) {
        return ChapterUpdateDtoBuilder.builder().estimatedMinutes(estimatedMinutes).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithCompletedMinutesAndEstimatedMinutes(final int completedMinutes, final int estimatedMinutes) {
        return ChapterUpdateDtoBuilder.builder().completedMinutes(completedMinutes).estimatedMinutes(estimatedMinutes).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithNullFields() {
        return ChapterUpdateDtoBuilder.builder().estimatedMinutes(null).completedMinutes(null).build().build();
    }
}