package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends MongoRepository<Tag, TagId> {
    List<Tag> findAllByOwner(UserId owner);
    Optional<Tag> findByIdAndOwner(TagId id, UserId owner);
    long countByOwnerAndIdIn(UserId owner, Collection<TagId> ids);
    boolean existsByIdAndOwner(TagId id, UserId owner);
}