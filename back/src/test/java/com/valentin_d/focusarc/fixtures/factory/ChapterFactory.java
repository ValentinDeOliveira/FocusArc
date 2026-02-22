package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.fixtures.arc.ArcCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcUpdateDtoBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterUpdateDtoBuilder;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;

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

    public static ArcCreationDto anArcCreationDtoWithOwnerId(final UserId ownerId) {
        return ArcCreationDtoBuilder.builder().ownerId(ownerId).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDto() {
        return ChapterUpdateDtoBuilder.builder().build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithNullFields() {
        return ArcUpdateDtoBuilder.builder().totalPlannedMinutes(null).name(null).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithTotalPlannedMinutes(final int totalPlannedMinutes) {
        return ArcUpdateDtoBuilder.builder().totalPlannedMinutes(totalPlannedMinutes).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithName(final String name) {
        return ArcUpdateDtoBuilder.builder().name(name).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithTotalPlannedMinutesAndName(final int totalPlannedMinutes, final String name) {
        return ArcUpdateDtoBuilder.builder().totalPlannedMinutes(totalPlannedMinutes).name(name).build().build();
    }
}