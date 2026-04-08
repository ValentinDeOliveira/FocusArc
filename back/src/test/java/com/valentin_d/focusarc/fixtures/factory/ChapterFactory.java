package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterSummaryResponseDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.fixtures.chapter.ChapterBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterSummaryResponseDtoBuilder;
import com.valentin_d.focusarc.fixtures.chapter.ChapterUpdateDtoBuilder;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.task.Task;

import java.time.LocalDate;
import java.util.List;

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

    public static Chapter aChapterWithScheduledDateAndArcIdAndAllTasksDone(final LocalDate date, final ArcId arcId,
                                                                           final boolean allTasksDone) {
        return ChapterBuilder.builder().scheduledDate(date).arc(arcId).allTasksDone(allTasksDone).build().build();
    }

    public static Chapter aChapterWithScheduledDateAndArcIdAndCompletedMinutesAndAllTasksCompleted(
            final LocalDate date, final ArcId arcId, final int completedMinutes, final boolean allTasksCompleted) {
        return ChapterBuilder.builder().scheduledDate(date).arc(arcId).completedMinutes(completedMinutes)
                .allTasksDone(allTasksCompleted).build().build();
    }

    public static ChapterCreationDto aChapterCreationDto() {
        return ChapterCreationDtoBuilder.builder().build().build();
    }

    public static ChapterCreationDto aChapterCreationDtoWithArcId(final ArcId arcId) {
        return ChapterCreationDtoBuilder.builder().arcId(arcId).build().build();
    }

    public static ChapterCreationDto aChapterCreationDtoWithArcIdAndEstimatedMinutes(final ArcId arcId,
                                                                                     final int estimatedMinutes) {
        return ChapterCreationDtoBuilder.builder().arcId(arcId).estimatedMinutes(estimatedMinutes).build().build();
    }

    public static ChapterCreationDto aChapterCreationDtoWithArcIdAndScheduledDate(final ArcId arcId, final LocalDate date) {
        return ChapterCreationDtoBuilder.builder().arcId(arcId).scheduledDate(date).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDto() {
        return ChapterUpdateDtoBuilder.builder().build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithScheduledDate(final LocalDate date) {
        return ChapterUpdateDtoBuilder.builder().scheduledDate(date).build().build();
    }

    public static ChapterUpdateDto aChapterUpdateDtoWithNullFields() {
        return ChapterUpdateDtoBuilder.builder().scheduledDate(null).build().build();
    }

    public static ChapterSummaryResponseDto aChapterSummaryResponseDtoWithTasks(final List<Task> tasks) {
        return ChapterSummaryResponseDtoBuilder.builder().tasksToComplete(tasks).build().build();
    }
}