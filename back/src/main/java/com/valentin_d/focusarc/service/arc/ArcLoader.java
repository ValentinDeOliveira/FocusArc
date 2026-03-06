package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.exception.ArcAlreadyExistsException;
import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.exception.NoActiveArcException;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.BaseService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArcLoader extends BaseService {
    private final ArcRepository arcRepository;

    public Arc getArcIfExists(@NotNull final ArcId arcId) {
        return fetchOrThrow(arcRepository, arcId, () -> new ArcDoesNotExistException(arcId));
    }

    public Optional<Arc> getArcByIdAndOwnerId(@NotNull final ArcId arcId, @NotNull final UserId ownerId) {
        return arcRepository.findByIdAndOwner(arcId, ownerId);
    }

    public void assertNotAnotherActiveArc(final UserId userId) {
        if (arcRepository.existsByOwnerAndStatus(userId, ArcStatus.ACTIVE)) {
            throw new ArcAlreadyExistsException(userId);
        }
    }

    public Arc getActiveArcForUser(final UserId userId) {
        return arcRepository.findByOwnerAndStatus(userId, ArcStatus.ACTIVE).orElseThrow(() -> new NoActiveArcException(userId));
    }

    public void assertOwnership(@NotNull final Arc arc, @NotNull final UserId userId) {
        if (!arc.getOwner().equals(userId)) {
            throw new AccessDeniedException("You do not own this arc");
        }
    }

    public void assertArcExistsForUser(final ArcId arcId, final UserId userId) {
        if (!arcRepository.existsByIdAndOwner(arcId, userId)) {
            throw new ArcDoesNotExistForUserException(arcId, userId);
        }
    }
}