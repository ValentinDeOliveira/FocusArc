package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.ChapterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private ArcRepository arcRepository;

    @InjectMocks
    private ChapterService service;

    @Test
    void shouldCreateChapter_whenArcExist() {
        final var creationDto = aChapterCreationDto();
        when(arcRepository.existsById(any())).thenReturn(true);

        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(creationDto.arcId(), result.getArc());
        assertEquals(creationDto.estimatedMinutes(), result.getEstimatedMinutes());
        assertEquals(0, result.getCompletedMinutes());
        assertNotNull(result.getId());

        verify(arcRepository).existsById(any(ArcId.class));
        verify(chapterRepository).save(any(Chapter.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenArcNotFound() {
        final var creationDto = aChapterCreationDto();

        when(arcRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.arcId().id().toString()));

        verify(arcRepository).existsById(creationDto.arcId());
        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void shouldUpdate_whenChapterExists() {
        final var chapter = aChapter();
        final var updateDto = aChapterUpdateDto();

        when(chapterRepository.findById(chapter.getId())).thenReturn(Optional.of(chapter));

        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var updated = service.update(chapter.getId(), updateDto);

        verify(chapterRepository).save(chapter);
        verify(chapterRepository).findById(chapter.getId());

        assertEquals(updated.getId(), chapter.getId());
        assertEquals(updated.getEstimatedMinutes(), updateDto.estimatedMinutes());
        assertEquals(updated.getCompletedMinutes(), updateDto.completedMinutes());
        assertEquals(updated.getArc(), chapter.getArc());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenChapterDoesNotExists() {
        final var chapter = aChapter();
        final var updateDto = aChapterUpdateDto();

        when(chapterRepository.findById(chapter.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(chapter.getId(), updateDto))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id()));

        verify(chapterRepository, never()).save(any(Chapter.class));
        verify(chapterRepository).findById(chapter.getId());
    }

    @Test
    void shouldDeleteArc_whenChapterExists() {
        final var chapter = aChapter();

        when(chapterRepository.findById(chapter.getId())).thenReturn(Optional.of(chapter));

        service.delete(chapter.getId());

        verify(chapterRepository).findById(chapter.getId());
        verify(chapterRepository).delete(chapter);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenChapterDoesNotExists() {
        final var chapter = aChapter();

        when(chapterRepository.findById(chapter.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(chapter.getId()))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id()));

        verify(chapterRepository, never()).delete(any(Chapter.class));
        verify(chapterRepository).findById(chapter.getId());
    }

    @Test
    void shouldDeleteAllChapters_whenArcExists() {
        final var arc = anArc();
        final var chapter = aChapterWithArcId(arc.getId());

        when(arcRepository.existsById(arc.getId())).thenReturn(true);
        when(chapterRepository.findAllByArc(arc.getId())).thenReturn(List.of(chapter));

        service.deleteAllForArc(arc.getId());

        verify(arcRepository).existsById(arc.getId());
        verify(chapterRepository).deleteAll(List.of(chapter));
    }

    @Test
    void shouldThrowExceptionOnDeleteAllChapters_whenArcDoesNotExists() {
        final var arc = anArc();

        when(arcRepository.existsById(arc.getId())).thenReturn(false);

        assertThatThrownBy(() -> service.deleteAllForArc(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(chapterRepository, never()).deleteAll(anyList());
        verify(arcRepository).existsById(arc.getId());
    }

    @Test
    void shouldGetAllChapterForArc_whenArcExists() {
        final var arc = anArc();

        when(arcRepository.existsById(arc.getId())).thenReturn(true);

        service.findAllForArc(arc.getId());

        verify(arcRepository).existsById(arc.getId());
        verify(chapterRepository).findAllByArc(arc.getId());
    }

    @Test
    void shouldThrowExceptionOnGetAllChaptersForArc_whenArcDoesNotExists() {
        final var arc = anArc();

        when(arcRepository.existsById(arc.getId())).thenReturn(false);

        assertThatThrownBy(() -> service.findAllForArc(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository).existsById(arc.getId());
        verify(chapterRepository, never()).findAllByArc(arc.getId());
    }
}