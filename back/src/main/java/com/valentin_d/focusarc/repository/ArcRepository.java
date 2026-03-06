package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ArcRepository extends MongoRepository<Arc, ArcId> {
    List<Arc> findAllByOwner(final UserId userId);

    boolean existsByOwnerAndStatus(final UserId userId, final ArcStatus status);

    Optional<Arc> findByOwnerAndStatus(final UserId userId, final ArcStatus status);

    Optional<Arc> findByIdAndOwner(final ArcId id, final UserId owner);
}