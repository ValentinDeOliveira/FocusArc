package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
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

    public Arc create(@NotNull UserId userId, @NotNull ArcCreationDto arcCreationDto) {
        userLoader.assertUserExists(userId);
        arcLoader.assertNotAnotherActiveArc(userId);

        final var arc = new Arc(userId, arcCreationDto.name(), arcCreationDto.totalEstimatedMinutes());
        return arcRepository.save(arc);
    }

    public Arc update(@NotNull UserId userId, @NotNull ArcId arcId, @NotNull ArcUpdateDto arcUpdateDto) {
        final var arc = arcLoader.getArcIfExists(arcId);
        arcLoader.assertOwnership(arc, userId);

        if (arcUpdateDto.name() != null) arc.setName(arcUpdateDto.name());
        if (arcUpdateDto.totalEstimatedMinutes() != null) arc.setTotalEstimatedMinutes(arcUpdateDto.totalEstimatedMinutes());

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
}