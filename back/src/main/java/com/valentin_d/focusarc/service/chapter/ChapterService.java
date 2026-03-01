package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterSummaryResponseDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.service.ContextLoader;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.arc.ArcRecalculationService;
import com.valentin_d.focusarc.service.task.TaskLoader;
import com.valentin_d.focusarc.service.task.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final ChapterLoader chapterLoader;
    private final ArcLoader arcLoader;
    private final TaskLoader taskLoader;
    private final ContextLoader contextLoader;
    private final ArcRecalculationService arcRecalculationService;
    private final TaskService taskService;

    public Optional<Chapter> findById(final ChapterId chapterId) {
        return chapterRepository.findById(chapterId);
    }

    public List<Chapter> findAllForArc(final ArcId arcId) {
        arcLoader.assertArcExists(arcId);

        return chapterRepository.findAllByArc(arcId);
    }

    public Chapter create(@NotNull final ChapterCreationDto chapterCreationDto) {
        arcLoader.assertArcExists(chapterCreationDto.arcId());
        chapterLoader.assertNotAlreadyExists(chapterCreationDto.arcId(), chapterCreationDto.scheduledDate());

        final var chapter = new Chapter(chapterCreationDto.arcId(), chapterCreationDto.estimatedMinutes(),
                chapterCreationDto.scheduledDate());
        return chapterRepository.save(chapter);
    }

    public Chapter update(@NotNull final ChapterId chapterId, @NotNull final ChapterUpdateDto chapterUpdateDto) {
        final var chapter = chapterLoader.getChapterIfExists(chapterId);
        // TODO: recalculate arc
        if (chapterUpdateDto.scheduledDate() != null) chapter.setScheduledDate(chapterUpdateDto.scheduledDate());

        return chapterRepository.save(chapter);
    }

    public void delete(@NotNull final ChapterId chapterId) {
        final var chapter = chapterLoader.getChapterIfExists(chapterId);
        taskService.deleteAllForChapter(chapterId);
        chapterRepository.delete(chapter);

        arcRecalculationService.recalculateEstimatedMinutes(chapter.getArc());
        arcRecalculationService.recalculateCompletedMinutes(chapter.getArc());
    }

    public void deleteAllForArc(@NotNull final ArcId arcId) {
        arcLoader.assertArcExists(arcId);

        final var chapters = chapterRepository.findAllByArc(arcId);

        chapters.forEach(ch -> taskService.deleteAllForChapter(ch.getId()));
        chapterRepository.deleteAll(chapters);

        arcRecalculationService.recalculateEstimatedMinutes(arcId);
        arcRecalculationService.recalculateCompletedMinutes(arcId);
    }

    public ChapterSummaryResponseDto getChapterSummary(@NotNull final UserId userId) {
        final var chapter = contextLoader.getChapterFromUserId(userId);
        final var tasksTodo = taskLoader.getNotCompletedTaskForChapter(chapter.getId());

        final var completedMinutes = tasksTodo.stream().mapToInt(Task::getCompletedMinutes).sum();
        final var remainingTime = chapter.getEstimatedMinutes() - completedMinutes;

        return new ChapterSummaryResponseDto(
                tasksTodo,
                chapter.getEstimatedMinutes(),
                completedMinutes,
                remainingTime
        );
    }
}