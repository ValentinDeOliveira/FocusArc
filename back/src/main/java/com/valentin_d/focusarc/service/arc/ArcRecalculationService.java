package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.ChapterRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class ArcRecalculationService {
    private final ArcRepository arcRepository;
    private final ChapterRepository chapterRepository;

    public void recalculateCompletedMinutes(@NotNull final ArcId arcId) {
        recalculateMinutes(arcId, Arc::recalculateCompletedMinutes);
    }

    public void recalculateEstimatedMinutes(@NotNull final ArcId arcId) {
        recalculateMinutes(arcId, Arc::recalculateEstimatedMinutes);
    }

    private void recalculateMinutes(@NotNull final ArcId arcId,
                                    final BiConsumer<Arc, List<Chapter>> arcRecalculator) {
        final var arc = arcRepository.findById(arcId)
                .orElseThrow(() -> new ArcDoesNotExistException(arcId));

        final var chapters = chapterRepository.findAllByArc(arcId);
        arcRecalculator.accept(arc, chapters);

        arcRepository.save(arc);
    }
}