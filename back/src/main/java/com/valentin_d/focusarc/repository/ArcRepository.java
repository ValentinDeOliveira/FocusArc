package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArcRepository extends MongoRepository<Arc, ArcId> {
    List<Arc> findAllByOwner(final UserId userId);
}