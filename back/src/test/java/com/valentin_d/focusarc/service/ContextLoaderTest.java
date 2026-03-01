package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.NoActiveArcException;
import com.valentin_d.focusarc.exception.NoChapterForArcException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.user.UserLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextLoaderTest {
    @Mock
    private ChapterLoader chapterLoader;
    @Mock
    private ArcLoader arcLoader;
    @Mock
    private UserLoader userLoader;
    @InjectMocks
    private ContextLoader service;

    @Test
    void shouldReturnChapter_whenDataIsValid() {
        final var userId = UserId.random();
        final var arc = anArc();
        final var chapter = aChapterWithArcId(arc.getId());

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);
        when(chapterLoader.findByDate(eq(arc.getId()), any())).thenReturn(chapter);

        service.getChapterFromUserId(userId);
        verify(userLoader).assertUserExists(userId);
        verify(arcLoader).getActiveArcForUser(userId);
        verify(chapterLoader).findByDate(eq(arc.getId()), any());
    }

    @Test
    void shouldThrowError_whenUserNotFound() {
        final var userId = UserId.random();

        doThrow(new UserDoesNotExistException(userId))
                .when(userLoader)
                .assertUserExists(eq(userId));

        assertThatThrownBy(() -> service.getChapterFromUserId(userId))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(userLoader).assertUserExists(userId);
        verify(arcLoader, never()).getActiveArcForUser(any(UserId.class));
        verify(chapterLoader, never()).findByDate(any(ArcId.class), any(LocalDate.class));
    }

    @Test
    void shouldThrowError_whenArcNotFound() {
        final var userId = UserId.random();

        when(arcLoader.getActiveArcForUser(userId))
                .thenThrow(new NoActiveArcException(userId));

        assertThatThrownBy(() -> service.getChapterFromUserId(userId))
                .isInstanceOf(NoActiveArcException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(userLoader).assertUserExists(userId);
        verify(arcLoader).getActiveArcForUser(userId);
        verify(chapterLoader, never()).findByDate(any(ArcId.class), any(LocalDate.class));
    }

    @Test
    void shouldThrowError_whenChapterNotFound() {
        final var userId = UserId.random();
        final var arc = anArc();

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);
        when(chapterLoader.findByDate(eq(arc.getId()), any(LocalDate.class)))
                .thenThrow(new NoChapterForArcException(arc.getId(), LocalDate.now()));

        assertThatThrownBy(() -> service.getChapterFromUserId(userId))
                .isInstanceOf(NoChapterForArcException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(userLoader).assertUserExists(userId);
        verify(arcLoader).getActiveArcForUser(userId);
        verify(chapterLoader).findByDate(eq(arc.getId()), any(LocalDate.class));
    }
}