package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChapterRepository extends MongoRepository<Chapter, ChapterId> {
    List<Chapter> findAllByArc(ArcId arcId);

    List<Chapter> arc(ArcId arc);
}