package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ArcRepository arcRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
    private final ArcService arcService;

    public Optional<Chapter> findById(final ChapterId chapterId) {
        return chapterRepository.findById(chapterId);
    }

    public List<Chapter> findAllForArc(final ArcId arcId) {
        assertArcExists(arcId);

        return chapterRepository.findAllByArc(arcId);
    }

    public Chapter create(@NotNull final ChapterCreationDto chapterCreationDto) {
        assertArcExists(chapterCreationDto.arcId());

        final var chapter = new Chapter(chapterCreationDto.arcId(), chapterCreationDto.estimatedMinutes());
        return chapterRepository.save(chapter);
    }

    public Chapter update(@NotNull final ChapterId chapterId, @NotNull final ChapterUpdateDto chapterUpdateDto) {
        final var chapter = getChapterIfExists(chapterId);

        if (chapterUpdateDto.completedMinutes() != null) chapter.setCompletedMinutes(chapterUpdateDto.completedMinutes());
        if (chapterUpdateDto.estimatedMinutes() != null) chapter.setEstimatedMinutes(chapterUpdateDto.estimatedMinutes());

        return chapterRepository.save(chapter);
    }

    public void delete(@NotNull final ChapterId chapterId) {
        final var chapter = getChapterIfExists(chapterId);
        chapterRepository.delete(chapter);
    }

    public void deleteAllForArc(@NotNull final ArcId arcId) {
        assertArcExists(arcId);

        final var chapters = chapterRepository.findAllByArc(arcId);
        chapterRepository.deleteAll(chapters);
    }

    void recalculateCompletedMinutes(@NotNull final ChapterId chapterId) {
        recalculateMinutes(
                chapterId,
                Chapter::recalculateCompletedMinutes,
                arcService::recalculateCompletedMinutes
        );
    }

    void recalculateEstimatedMinutes(@NotNull final ChapterId chapterId) {
        recalculateMinutes(
                chapterId,
                Chapter::recalculateEstimatedMinutes,
                arcService::recalculateEstimatedMinutes
        );
    }

    private void recalculateMinutes(@NotNull final ChapterId chapterId,
                            final BiConsumer<Chapter, List<Task>> chapterRecalculator,
                            final Consumer<ArcId> arcRecalculator) {
        final var chapter = getChapterIfExists(chapterId);

        final var tasks = taskRepository.findAllByChapter(chapterId);
        chapterRecalculator.accept(chapter, tasks);

        chapterRepository.save(chapter);
        arcRecalculator.accept(chapter.getArc());
    }

    private void assertArcExists(final ArcId arcId) {
        if (!arcRepository.existsById(arcId)) {
            throw new ArcDoesNotExistException(arcId);
        }
    }

    private Chapter getChapterIfExists(@NotNull final ChapterId chapterId) {
        return findById(chapterId).orElseThrow(() -> new ChapterDoesNotExistException(chapterId));
    }
}