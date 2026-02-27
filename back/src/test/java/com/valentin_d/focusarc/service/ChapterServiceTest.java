package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    private ChapterLoader chapterLoader;
    @Mock
    private ArcLoader arcLoader;

    @InjectMocks
    private ChapterService service;

    @Test
    void shouldCreateChapter_whenArcExist() {
        final var creationDto = aChapterCreationDto();

        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(creationDto.arcId(), result.getArc());
        assertEquals(creationDto.estimatedMinutes(), result.getEstimatedMinutes());
        assertEquals(0, result.getCompletedMinutes());
        assertNotNull(result.getId());

        verify(chapterRepository).save(any(Chapter.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenArcNotFound() {
        final var creationDto = aChapterCreationDto();

        doThrowArcDoesNotExist(creationDto.arcId());

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.arcId().id().toString()));

        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void shouldUpdate_whenChapterExists() {
        final var chapter = aChapter();
        final var updateDto = aChapterUpdateDto();

        when(chapterLoader.getChapterIfExists(chapter.getId())).thenReturn(chapter);

        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var updated = service.update(chapter.getId(), updateDto);

        verify(chapterRepository).save(chapter);

        assertEquals(updated.getId(), chapter.getId());
        assertEquals(updated.getEstimatedMinutes(), updateDto.estimatedMinutes());
        assertEquals(updated.getCompletedMinutes(), updateDto.completedMinutes());
        assertEquals(updated.getArc(), chapter.getArc());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenChapterDoesNotExists() {
        final var chapter = aChapter();
        final var updateDto = aChapterUpdateDto();

        when(chapterLoader.getChapterIfExists(eq(chapter.getId())))
                .thenThrow((new ChapterDoesNotExistException(chapter.getId())));

        assertThatThrownBy(() -> service.update(chapter.getId(), updateDto))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id()));

        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void shouldDeleteArc_whenChapterExists() {
        final var chapter = aChapter();

        when(chapterLoader.getChapterIfExists(chapter.getId())).thenReturn(chapter);

        service.delete(chapter.getId());

        verify(chapterRepository).delete(chapter);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenChapterDoesNotExists() {
        final var chapter = aChapter();

        when(chapterLoader.getChapterIfExists(eq(chapter.getId())))
                .thenThrow((new ChapterDoesNotExistException(chapter.getId())));

        assertThatThrownBy(() -> service.delete(chapter.getId()))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id()));

        verify(chapterRepository, never()).delete(any(Chapter.class));
    }

    @Test
    void shouldDeleteAllChapters_whenArcExists() {
        final var arc = anArc();
        final var chapter = aChapterWithArcId(arc.getId());

        when(chapterRepository.findAllByArc(arc.getId())).thenReturn(List.of(chapter));

        service.deleteAllForArc(arc.getId());

        verify(chapterRepository).deleteAll(List.of(chapter));
    }

    @Test
    void shouldThrowExceptionOnDeleteAllChapters_whenArcDoesNotExists() {
        final var arc = anArc();

        doThrowArcDoesNotExist(arc.getId());

        assertThatThrownBy(() -> service.deleteAllForArc(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(chapterRepository, never()).deleteAll(anyList());
    }

    @Test
    void shouldGetAllChapterForArc_whenArcExists() {
        final var arc = anArc();

        service.findAllForArc(arc.getId());

        verify(chapterRepository).findAllByArc(arc.getId());
    }

    @Test
    void shouldThrowExceptionOnGetAllChaptersForArc_whenArcDoesNotExists() {
        final var arc = anArc();

        doThrowArcDoesNotExist(arc.getId());

        assertThatThrownBy(() -> service.findAllForArc(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(chapterRepository, never()).findAllByArc(arc.getId());
    }

    private void doThrowArcDoesNotExist(final ArcId arcId) {
        doThrow(new ArcDoesNotExistException(arcId))
                .when(arcLoader)
                .assertArcExists(eq(arcId));
    }
}