package com.valentin_d.focusarc.service.chapter;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.service.BaseService;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChapterService extends BaseService {
    private final ChapterRepository chapterRepository;
    private final ChapterLoader chapterLoader;
    private final ArcLoader arcLoader;

    public Optional<Chapter> findById(final ChapterId chapterId) {
        return chapterRepository.findById(chapterId);
    }

    public List<Chapter> findAllForArc(final ArcId arcId) {
        arcLoader.assertArcExists(arcId);

        return chapterRepository.findAllByArc(arcId);
    }

    public Chapter create(@NotNull final ChapterCreationDto chapterCreationDto) {
        arcLoader.assertArcExists(chapterCreationDto.arcId());

        final var chapter = new Chapter(chapterCreationDto.arcId(), chapterCreationDto.estimatedMinutes());
        return chapterRepository.save(chapter);
    }

    public Chapter update(@NotNull final ChapterId chapterId, @NotNull final ChapterUpdateDto chapterUpdateDto) {
        final var chapter = chapterLoader.getChapterIfExists(chapterId);

        if (chapterUpdateDto.completedMinutes() != null) chapter.setCompletedMinutes(chapterUpdateDto.completedMinutes());
        if (chapterUpdateDto.estimatedMinutes() != null) chapter.setEstimatedMinutes(chapterUpdateDto.estimatedMinutes());

        return chapterRepository.save(chapter);
    }

    public void delete(@NotNull final ChapterId chapterId) {
        final var chapter = chapterLoader.getChapterIfExists(chapterId);
        chapterRepository.delete(chapter);
    }

    public void deleteAllForArc(@NotNull final ArcId arcId) {
        arcLoader.assertArcExists(arcId);

        final var chapters = chapterRepository.findAllByArc(arcId);
        chapterRepository.deleteAll(chapters);
    }
}