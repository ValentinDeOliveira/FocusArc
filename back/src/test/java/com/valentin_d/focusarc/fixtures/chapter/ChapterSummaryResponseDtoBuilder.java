package com.valentin_d.focusarc.fixtures.chapter;

import com.valentin_d.focusarc.dto.chapter.ChapterSummaryResponseDto;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import lombok.Builder;

import java.util.List;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTask;

@Builder
public class ChapterSummaryResponseDtoBuilder {
    @Builder.Default
    private final ChapterId chapterId = ChapterId.random();
    @Builder.Default
    private final List<Task> tasksToComplete = List.of(aTask());
    @Builder.Default
    private final int estimatedMinutes = 250;
    @Builder.Default
    private final int completedMinutes = 50;
    @Builder.Default
    private final int remainingTime = 200;


    public ChapterSummaryResponseDto build() {
        return new ChapterSummaryResponseDto(
                chapterId, tasksToComplete, estimatedMinutes, completedMinutes, remainingTime
        );
    }
}