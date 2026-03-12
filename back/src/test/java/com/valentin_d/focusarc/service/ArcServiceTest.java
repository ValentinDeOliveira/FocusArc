package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.arc.ArcAlreadyExistsException;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.exception.arc.NoActiveArcException;
import com.valentin_d.focusarc.exception.user.UserDoesNotExistException;
import com.valentin_d.focusarc.fixtures.chapter.ChapterBuilder;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.arc.ArcService;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.service.user.UserLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArcServiceTest {
    @Mock
    private ArcRepository arcRepository;
    @Mock
    private ArcLoader arcLoader;
    @Mock
    private UserLoader userLoader;
    @Mock
    private ChapterService chapterService;
    @Mock
    private ChapterLoader chapterLoader;
    @InjectMocks
    private ArcService arcService;

    @Test
    void shouldCreateArc_whenUserDoesExist() {
        final var userId = UserId.random();
        final var creationDto = anArcCreationDto();

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = arcService.create(userId, creationDto);

        assertEquals(userId, result.getOwner());
        assertEquals(creationDto.name(), result.getName());
        assertEquals(0, result.getTotalCompletedMinutes());
        assertEquals(creationDto.totalEstimatedMinutes(), result.getTotalEstimatedMinutes());

        verify(arcRepository).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenUserNotFound() {
        final var userId = UserId.random();
        final var creationDto = anArcCreationDto();

        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> arcService.create(userId, creationDto))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id().toString()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenAnotherActiveArcExist() {
        final var userId = UserId.random();
        final var creationDto = anArcCreationDto();

        doThrow(new ArcAlreadyExistsException(userId))
                .when(arcLoader)
                .assertNotAnotherActiveArc(eq(userId));

        assertThatThrownBy(() -> arcService.create(userId, creationDto))
                .isInstanceOf(ArcAlreadyExistsException.class)
                .hasMessageContaining(String.valueOf(userId.id().toString()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldUpdate_whenArcExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());
        final var updateDto = anArcUpdateDto();

        when(arcLoader.getArcIfExists(eq(arc.getId()))).thenReturn(arc);

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Arc updated = arcService.update(user.getId(), arc.getId(), updateDto);

        verify(arcRepository).save(arc);

        assertEquals(arc.getName(), updated.getName());
        assertEquals(arc.getTotalEstimatedMinutes(), updated.getTotalEstimatedMinutes());
        assertEquals(arc.getId(), updated.getId());
        assertEquals(arc.getOwner(), updated.getOwner());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenArcDoesNotExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());
        final var updateDto = anArcUpdateDto();

        when(arcLoader.getArcIfExists(eq(arc.getId())))
                .thenThrow((new ArcDoesNotExistException(arc.getId())));

        assertThatThrownBy(() -> arcService.update(user.getId(), arc.getId(), updateDto))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldDeleteArc_whenArcExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        when(arcLoader.getArcIfExists(arc.getId())).thenReturn(arc);

        arcService.delete(user.getId(), arc.getId());

        verify(arcRepository).delete(arc);
        verify(chapterService).deleteAllForArc(arc.getId(), user.getId());
    }

    @Test
    void shouldThrowExceptionOnDelete_whenArcDoesNotExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        when(arcLoader.getArcIfExists(eq(arc.getId())))
                .thenThrow((new ArcDoesNotExistException(arc.getId())));

        assertThatThrownBy(() -> arcService.delete(user.getId(), arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
    }

    @Test
    void shouldDeleteAllArcs_whenUserExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        when(arcRepository.findAllByOwner(user.getId())).thenReturn(List.of(arc));

        arcService.deleteAllForUser(user.getId());

        verify(arcRepository).deleteAll(List.of(arc));
        verify(chapterService).deleteAllForArc(arc.getId(), user.getId());
    }

    @Test
    void shouldThrowExceptionOnDeleteAllArcs_whenUserDoesNotExists() {
        final var userId = UserId.random();

        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> arcService.deleteAllForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(arcRepository, never()).deleteAll(anyList());
    }

    @Test
    void shouldGetAllArcsForUser_whenUserExists() {
        final var arc = anArc();
        when(arcRepository.findAllByOwner(arc.getOwner())).thenReturn(List.of(arc));

        final var result = arcService.findAllForUser(arc.getOwner());

        assertEquals(List.of(arc), result);
    }

    @Test
    void shouldThrowExceptionOnGetAllArcsForUser_whenUserDoesNotExists() {
        final var arc = anArc();

        doThrowUserDoesNotExist(arc.getOwner());

        assertThatThrownBy(() -> arcService.findAllForUser(arc.getOwner()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getOwner().id()));

        verify(arcRepository, never()).findAllByOwner(any(UserId.class));
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenUserDoesNotOwnArc() {
        final var owner = aUser();
        final var attacker = aUser();
        final var arc = anArcWithOwnerId(owner.getId());
        final var updateDto = anArcUpdateDto();

        when(arcLoader.getArcIfExists(arc.getId())).thenReturn(arc);
        doThrowArcDoesNotExistForUser(arc, attacker.getId());

        assertThatThrownBy(() -> arcService.update(attacker.getId(), arc.getId(), updateDto))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnDelete_whenUserDoesNotOwnArc() {
        final var owner = aUser();
        final var attacker = aUser();
        final var arc = anArcWithOwnerId(owner.getId());

        when(arcLoader.getArcIfExists(arc.getId())).thenReturn(arc);
        doThrowArcDoesNotExistForUser(arc, attacker.getId());

        assertThatThrownBy(() -> arcService.delete(attacker.getId(), arc.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(arcRepository, never()).delete(any(Arc.class));
    }

    @Test
    void shouldReturnSummary_whenUserHasActiveArc() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerId(userId);
        arc.setTotalCompletedMinutes(60);

        final var yesterday = ChapterBuilder.builder()
                .arc(arc.getId()).scheduledDate(LocalDate.now().minusDays(1)).completedMinutes(60).build().build();
        final var threeDaysAgo = ChapterBuilder.builder()
                .arc(arc.getId()).scheduledDate(LocalDate.now().minusDays(3)).completedMinutes(0).build().build();
        final var tomorrow = ChapterBuilder.builder()
                .arc(arc.getId()).scheduledDate(LocalDate.now().plusDays(1)).completedMinutes(0).build().build();

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);
        when(chapterLoader.findAllByArc(arc.getId())).thenReturn(List.of(yesterday, threeDaysAgo, tomorrow));

        final var result = arcService.getSummaryForUser(userId);

        assertThat(result.totalEstimatedMinutes()).isEqualTo(arc.getTotalEstimatedMinutes());
        assertThat(result.totalCompletedMinutes()).isEqualTo(60);
        assertThat(result.remainingMinutes()).isEqualTo(arc.getTotalEstimatedMinutes() - 60);
        assertThat(result.nbChapterCompleted()).isEqualTo(1);
        assertThat(result.nbChapterSkipped()).isEqualTo(1);
        assertThat(result.nbChapterPlanned()).isEqualTo(1);
        assertThat(result.daysStreak()).isEqualTo(1);
    }

    @Test
    void shouldThrowException_whenUserDoesNotExist_onGetSummary() {
        final var userId = UserId.random();
        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> arcService.getSummaryForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(arcLoader, never()).getActiveArcForUser(any());
    }

    @Test
    void shouldThrowException_whenNoActiveArc_onGetSummary() {
        final var userId = UserId.random();
        doThrow(new NoActiveArcException(userId)).when(arcLoader).getActiveArcForUser(userId);

        assertThatThrownBy(() -> arcService.getSummaryForUser(userId))
                .isInstanceOf(NoActiveArcException.class);

        verify(chapterLoader, never()).findAllByArc(any());
    }

    private void doThrowUserDoesNotExist(final UserId userId) {
        doThrow(new UserDoesNotExistException(userId))
                .when(userLoader)
                .assertUserExists(eq(userId));
    }

    private void doThrowArcDoesNotExistForUser(final Arc arc, final UserId userId) {
        doThrow(new ArcDoesNotExistForUserException(arc.getId(), userId))
                .when(arcLoader)
                .assertOwnership(arc, userId);
    }
}