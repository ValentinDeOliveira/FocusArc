package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.arc.ArcRecalculationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
@Validated
@RequiredArgsConstructor
public class ChapterRecalculationService {
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
    private final ChapterLoader chapterLoader;
    private final ArcRecalculationService arcRecalculationService;

    public void recalculateCompletedMinutes(@NotNull ChapterId chapterId) {
        recalculateMinutes(
                chapterId,
                Chapter::recalculateCompletedMinutes,
                arcRecalculationService::recalculateCompletedMinutes
        );
    }

    public void recalculateEstimatedMinutes(@NotNull ChapterId chapterId) {
        recalculateMinutes(
                chapterId,
                Chapter::recalculateEstimatedMinutes,
                arcRecalculationService::recalculateEstimatedMinutes
        );
    }

    private void recalculateMinutes(ChapterId chapterId,
                            BiConsumer<Chapter, List<Task>> chapterRecalculator,
                            Consumer<ArcId> arcRecalculator) {
        final var chapter = chapterLoader.getChapterIfExists(chapterId);

        final var tasks = taskRepository.findAllByChapterOrderByStartAtAsc(chapterId);
        chapterRecalculator.accept(chapter, tasks);

        chapterRepository.save(chapter);
        arcRecalculator.accept(chapter.getArc());
    }
}