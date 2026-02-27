package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.ArcAlreadyExistsException;
import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.arc.ArcService;
import com.valentin_d.focusarc.service.user.UserLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
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

    @InjectMocks
    private ArcService service;

    @Test
    void shouldCreateArc_whenUserDoesExist() {
        final var creationDto = anArcCreationDto();

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(creationDto.ownerId(), result.getOwner());
        assertEquals(creationDto.name(), result.getName());
        assertEquals(0, result.getTotalCompletedMinutes());
        assertEquals(creationDto.totalEstimatedMinutes(), result.getTotalEstimatedMinutes());

        verify(arcRepository).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenUserNotFound() {
        final var creationDto = anArcCreationDto();

        doThrowUserDoesNotExist(creationDto.ownerId());

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.ownerId().id().toString()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenAnotherActiveArcExist() {
        final var creationDto = anArcCreationDto();

        doThrow(new ArcAlreadyExistsException(creationDto.ownerId()))
                .when(arcLoader)
                .assertNotAnotherActiveArc(eq(creationDto.ownerId()));

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(ArcAlreadyExistsException.class)
                .hasMessageContaining(String.valueOf(creationDto.ownerId().id().toString()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldUpdate_whenArcExists() {
        final var arc = anArc();
        final var updateDto = anArcUpdateDto();

        when(arcLoader.getArcIfExists(eq(arc.getId()))).thenReturn(arc);

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Arc updated = service.update(arc.getId(), updateDto);

        verify(arcRepository).save(arc);

        assertEquals(arc.getName(), updated.getName());
        assertEquals(arc.getTotalEstimatedMinutes(), updated.getTotalEstimatedMinutes());
        assertEquals(arc.getId(), updated.getId());
        assertEquals(arc.getOwner(), updated.getOwner());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenArcDoesNotExists() {
        final var arc = anArc();
        final var updateDto = anArcUpdateDto();

        when(arcLoader.getArcIfExists(eq(arc.getId())))
                .thenThrow((new ArcDoesNotExistException(arc.getId())));

        assertThatThrownBy(() -> service.update(arc.getId(), updateDto))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldDeleteArc_whenArcExists() {
        final var arc = anArc();

        when(arcLoader.getArcIfExists(arc.getId())).thenReturn(arc);

        service.delete(arc.getId());

        verify(arcRepository).delete(arc);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenArcDoesNotExists() {
        final var arc = anArc();

        when(arcLoader.getArcIfExists(eq(arc.getId())))
                .thenThrow((new ArcDoesNotExistException(arc.getId())));

        assertThatThrownBy(() -> service.delete(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
    }

    @Test
    void shouldDeleteAllArcs_whenUserExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        when(arcRepository.findAllByOwner(user.getId())).thenReturn(List.of(arc));

        service.deleteAllForUser(user.getId());

        verify(arcRepository).deleteAll(List.of(arc));
    }

    @Test
    void shouldThrowExceptionOnDeleteAllArcs_whenUserDoesNotExists() {
        final var userId = UserId.random();

        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> service.deleteAllForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(arcRepository, never()).deleteAll(anyList());
    }

    @Test
    void shouldGetAllArcsForUser_whenUserExists() {
        final var arc = anArc();

        service.findAllForUser(arc.getOwner());

        verify(arcRepository).findAllByOwner(arc.getOwner());
    }

    @Test
    void shouldThrowExceptionOnGetAllArcsForUser_whenUserDoesNotExists() {
        final var arc = anArc();

        doThrowUserDoesNotExist(arc.getOwner());

        assertThatThrownBy(() -> service.findAllForUser(arc.getOwner()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getOwner().id()));

        verify(arcRepository, never()).findAllByOwner(any(UserId.class));
    }

    private void doThrowUserDoesNotExist(final UserId userId) {
        doThrow(new UserDoesNotExistException(userId))
                .when(userLoader)
                .assertUserExists(eq(userId));
    }
}