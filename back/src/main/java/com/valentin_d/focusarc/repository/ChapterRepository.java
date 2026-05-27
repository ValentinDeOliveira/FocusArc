package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends MongoRepository<Chapter, ChapterId> {
    List<Chapter> findAllByArc(ArcId arcId);

    boolean existsByArcAndScheduledDate(ArcId arc, LocalDate scheduledDate);

    Optional<Chapter> findByArcAndScheduledDate(ArcId arc, LocalDate scheduledDate);

    void deleteAllByArc(ArcId arcId);
}