package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface TagRepository extends MongoRepository<Tag, TagId> {
    long countByOwnerAndIdIn(UserId owner, Collection<TagId> ids);
}