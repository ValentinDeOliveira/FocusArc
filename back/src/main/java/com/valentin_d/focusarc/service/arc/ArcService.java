package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.user.UserLoader;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArcService {
    private final ArcRepository arcRepository;
    private final ArcLoader arcLoader;
    private final UserLoader userLoader;

    public Optional<Arc> findById(final ArcId arcId) {
        return arcRepository.findById(arcId);
    }

    public List<Arc> findAllForUser(final UserId userId) {
        userLoader.assertUserExists(userId);

        return arcRepository.findAllByOwner(userId);
    }

    public Arc create(@NotNull final ArcCreationDto arcCreationDto) {
        userLoader.assertUserExists(arcCreationDto.ownerId());

        final var arc = new Arc(arcCreationDto.ownerId(), arcCreationDto.name(), arcCreationDto.totalEstimatedMinutes());
        return arcRepository.save(arc);
    }

    public Arc update(@NotNull final ArcId arcId, @NotNull final ArcUpdateDto arcUpdateDto) {
        final var arc = arcLoader.getArcIfExists(arcId);

        if (arcUpdateDto.name() != null) arc.setName(arcUpdateDto.name());
        if (arcUpdateDto.totalEstimatedMinutes() != null) arc.setTotalEstimatedMinutes(arcUpdateDto.totalEstimatedMinutes());

        return arcRepository.save(arc);
    }

    public void delete(@NotNull final ArcId arcId) {
        final var arc = arcLoader.getArcIfExists(arcId);
        arcRepository.delete(arc);
    }

    public void deleteAllForUser(@NotNull final UserId userId) {
        userLoader.assertUserExists(userId);

        final var arcs = arcRepository.findAllByOwner(userId);
        arcRepository.deleteAll(arcs);
    }
}