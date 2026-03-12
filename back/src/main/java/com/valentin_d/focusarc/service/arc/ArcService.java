package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.exception.InvalidDateRangeException;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.service.user.UserLoader;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class ArcService {
    private final ArcRepository arcRepository;
    private final ArcLoader arcLoader;
    private final UserLoader userLoader;
    private final ChapterService chapterService;

    public Optional<Arc> findByIdAndOwnerId(@NotNull ArcId arcId, @NotNull UserId ownerId) {
        return arcLoader.getArcByIdAndOwnerId(arcId, ownerId);
    }

    public List<Arc> findAllForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);

        return arcRepository.findAllByOwner(userId);
    }

    public Arc create(@NotNull UserId userId, @NotNull ArcCreationDto dto) {
        userLoader.assertUserExists(userId);
        arcLoader.assertNotAnotherActiveArc(userId);

        final var arc = new Arc(userId, dto.name(), dto.totalEstimatedMinutes(), dto.startDate(), dto.endDate());
        return arcRepository.save(arc);
    }

    public Arc update(@NotNull UserId userId, @NotNull ArcId arcId, @NotNull ArcUpdateDto updateDto) {
        final var arc = arcLoader.getArcIfExists(arcId);
        arcLoader.assertOwnership(arc, userId);

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
        final var arc = arcLoader.getArcIfExists(arcId);
        arcLoader.assertOwnership(arc, userId);
        chapterService.deleteAllForArc(arcId, userId);
        arcRepository.delete(arc);
    }

    public void deleteAllForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);

        final var arcs = arcRepository.findAllByOwner(userId);
        arcs.forEach(arc -> chapterService.deleteAllForArc(arc.getId(), userId));
        arcRepository.deleteAll(arcs);
    }

    public ArcSummaryResponseDto getSummaryForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);
        return null;
    }
}