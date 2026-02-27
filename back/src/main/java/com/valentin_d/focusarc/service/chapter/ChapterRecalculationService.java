package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ChapterRecalculationService {
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
    private final ArcRecalculationService arcRecalculationService;

    public void recalculateCompletedMinutes(@NotNull final ChapterId chapterId) {
        recalculateMinutes(
                chapterId,
                Chapter::recalculateCompletedMinutes,
                arcRecalculationService::recalculateCompletedMinutes
        );
    }

    public void recalculateEstimatedMinutes(@NotNull final ChapterId chapterId) {
        recalculateMinutes(
                chapterId,
                Chapter::recalculateEstimatedMinutes,
                arcRecalculationService::recalculateEstimatedMinutes
        );
    }

    private void recalculateMinutes(@NotNull final ChapterId chapterId,
                            final BiConsumer<Chapter, List<Task>> chapterRecalculator,
                            final Consumer<ArcId> arcRecalculator) {
        final var chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterDoesNotExistException(chapterId));

        final var tasks = taskRepository.findAllByChapter(chapterId);
        chapterRecalculator.accept(chapter, tasks);

        chapterRepository.save(chapter);
        arcRecalculator.accept(chapter.getArc());
    }
}