package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.dto.tag.TagTaskStatsDto;
import com.valentin_d.focusarc.dto.task.TaskRecurrenceDto;
import com.valentin_d.focusarc.dto.task.TaskStatsDto;
import com.valentin_d.focusarc.exception.InvalidDateRangeException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.ChapterStatus;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.service.task.TaskLoader;
import com.valentin_d.focusarc.service.task.TaskService;
import com.valentin_d.focusarc.service.user.UserLoader;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class ArcService {
    private final ArcRepository arcRepository;
    private final ArcLoader arcLoader;
    private final UserLoader userLoader;
    private final ChapterService chapterService;
    private final ChapterLoader chapterLoader;
    private final TaskLoader taskLoader;
    private final TaskService taskService;

    public Optional<Arc> findByIdAndOwnerId(@NotNull ArcId arcId, @NotNull UserId ownerId) {
        return arcLoader.findArcByIdAndOwnerId(arcId, ownerId);
    }

    public List<Arc> findAllForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);

        return arcLoader.getAllByOwner(userId);
    }

    public Optional<Arc> findActiveArcForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);

        return arcLoader.findActiveArcForUser(userId);
    }

    public Arc create(@NotNull UserId userId, @NotNull ArcCreationDto dto) {
        userLoader.assertUserExists(userId);
        arcLoader.assertNotAnotherActiveArc(userId);

        final var arc = new Arc(userId, dto.name(), dto.startDate(), dto.endDate());
        return arcRepository.save(arc);
    }

    public Arc update(@NotNull UserId userId, @NotNull ArcId arcId, @NotNull ArcUpdateDto updateDto) {
        final var arc = arcLoader.getArcIfExistsForUser(arcId, userId);

        final var effectiveStart = updateDto.startDate() != null ? updateDto.startDate() : arc.getStartDate();
        final var effectiveEnd   = updateDto.endDate()   != null ? updateDto.endDate()   : arc.getEndDate();

        if (effectiveStart != null && effectiveEnd != null && !effectiveEnd.isAfter(effectiveStart)) {
            throw new InvalidDateRangeException(effectiveStart, effectiveEnd);
        }

        if (updateDto.name() != null) arc.setName(updateDto.name());
        if (updateDto.totalEstimatedMinutes() != null) arc.setTotalEstimatedMinutes(updateDto.totalEstimatedMinutes());
        if (updateDto.startDate() != null) arc.setStartDate(updateDto.startDate());
        if (updateDto.endDate() != null) arc.setEndDate(updateDto.endDate());

        return arcRepository.save(arc);
    }

    public void delete(@NotNull UserId userId, @NotNull ArcId arcId) {
        final var arc = arcLoader.getArcIfExistsForUser(arcId, userId);
        chapterService.deleteAllForArc(arcId, userId);
        arcRepository.delete(arc);
    }

    public void deleteAllForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);

        final var arcs = arcLoader.getAllByOwner(userId);
        arcs.forEach(arc -> chapterService.deleteAllForArc(arc.getId(), userId));
        arcRepository.deleteAll(arcs);
    }

    public ArcSummaryResponseDto getSummaryForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);

        final var arc = arcLoader.getActiveArcForUser(userId);
        final var chapters = chapterLoader.findAllByArc(arc.getId());
        final var chaptersByStatus = chapters.stream()
                .collect(Collectors.groupingBy(c -> c.getStatus(LocalDate.now()), Collectors.counting()));

        return new ArcSummaryResponseDto(
                arc.getId(),
                arc.getName(),
                arc.getTotalEstimatedMinutes(),
                arc.getTotalCompletedMinutes(),
                arc.getTotalEstimatedMinutes() - arc.getTotalCompletedMinutes(),
                chaptersByStatus.getOrDefault(ChapterStatus.COMPLETED, 0L).intValue(),
                chaptersByStatus.getOrDefault(ChapterStatus.PLANNED, 0L).intValue(),
                chaptersByStatus.getOrDefault(ChapterStatus.SKIPPED, 0L).intValue(),
                getStreak(chapters)
        );
    }

    public List<TagTaskStatsDto> getTagTaskStats(@NotNull UserId userId) {
        return taskLoader.getTagStatsForChapters(getChaptersForUser(userId));
    }

    public List<TaskStatsDto> getTaskStats(@NotNull UserId userId) {
        return taskLoader.getTaskStatsForChapters(getChaptersForUser(userId));
    }

    public void massCreate(@NotNull @Size(max = 5) List<TaskRecurrenceDto> taskMassCreationDto,
                           @NotNull ArcId arcId,
                           @NotNull UserId userId) {
        final var arc = arcLoader.getArcIfExistsForUser(arcId, userId);

        if (arc.getStartDate() == null || arc.getEndDate() == null) {
            throw new IllegalStateException("Arc must have a start and end date for mass task creation");
        }

        new MassTaskCreator(arc, userId, chapterLoader, chapterService, taskService)
                .execute(taskMassCreationDto);
    }

    private List<ChapterId> getChaptersForUser(UserId userId) {
        userLoader.assertUserExists(userId);

        final var arc = arcLoader.getActiveArcForUser(userId);
        return chapterLoader.findAllByArc(arc.getId())
                .stream()
                .map(Chapter::getId)
                .collect(Collectors.toList());
    }

    private int getStreak(List<Chapter> chapters) {
        final var today = LocalDate.now();

        final var sorted = chapters.stream()
                .filter(c -> !c.getScheduledDate().isAfter(today)) // exclude future chapters
                .filter(c -> !c.getScheduledDate().isEqual(today) || c.getCompletedMinutes() > 0) // today counts only if at least one task is done
                .sorted(Comparator.comparing(Chapter::getScheduledDate).reversed())
                .toList();

        // if today has no completed work yet, start counting from yesterday
        var expected = sorted.isEmpty() || !sorted.get(0).getScheduledDate().isEqual(today)
                ? today.minusDays(1)
                : today;

        int streak = 0;
        for (Chapter chapter : sorted) {
            // gap | not completed
            if (!chapter.getScheduledDate().isEqual(expected) || chapter.getCompletedMinutes() == 0) break;
            streak++;
            expected = expected.minusDays(1);
        }
        return streak;
    }

}