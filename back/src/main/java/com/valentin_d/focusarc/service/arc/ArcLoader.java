package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.exception.arc.ArcAlreadyExistsException;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.exception.arc.NoActiveArcException;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArcLoader extends BaseService {
    private final ArcRepository arcRepository;

    public Arc getArcIfExists(ArcId arcId) {
        return fetchOrThrow(arcRepository, arcId, () -> new ArcDoesNotExistException(arcId));
    }

    public Optional<Arc> getArcByIdAndOwnerId(ArcId arcId, UserId ownerId) {
        return arcRepository.findByIdAndOwner(arcId, ownerId);
    }

    public void assertNotAnotherActiveArc(UserId userId) {
        if (arcRepository.existsByOwnerAndStatus(userId, ArcStatus.ACTIVE)) {
            throw new ArcAlreadyExistsException(userId);
        }
    }

    public Arc getActiveArcForUser(UserId userId) {
        return arcRepository.findByOwnerAndStatus(userId, ArcStatus.ACTIVE).orElseThrow(() -> new NoActiveArcException(userId));
    }

    public Arc getArcIfExistsForUser(ArcId arcId, UserId userId) {
        return arcRepository.findByIdAndOwner(arcId, userId)
                .orElseThrow(() -> new ArcDoesNotExistForUserException(arcId, userId));
    }

    public void assertArcExistsForUser(ArcId arcId, UserId userId) {
        if (!arcRepository.existsByIdAndOwner(arcId, userId)) {
            throw new ArcDoesNotExistForUserException(arcId, userId);
        }
    }
}