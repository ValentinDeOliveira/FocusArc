package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArcServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArcRepository arcRepository;

    @InjectMocks
    private ArcService service;

    @Test
    void shouldCreateArc_whenUserDoesExist() {
        final var creationDto = anArcCreationDto();
        when(userRepository.existsById(any())).thenReturn(true);

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(creationDto.ownerId(), result.getOwner());
        assertEquals(creationDto.name(), result.getName());
        assertEquals(0, result.getTotalCompletedMinutes());
        assertEquals(creationDto.totalEstimatedMinutes(), result.getTotalEstimatedMinutes());

        verify(userRepository).existsById(any(UserId.class));
        verify(arcRepository).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenUserNotFound() {
        final var creationDto = anArcCreationDto();

        when(userRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.ownerId().id().toString()));

        verify(userRepository).existsById(creationDto.ownerId());
        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldUpdate_whenArcExists() {
        final var arc = anArc();
        final var updateDto = anArcUpdateDto();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.of(arc));

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Arc updated = service.update(arc.getId(), updateDto);

        verify(arcRepository).save(arc);
        verify(arcRepository).findById(arc.getId());

        assertEquals(arc.getName(), updated.getName());
        assertEquals(arc.getTotalEstimatedMinutes(), updated.getTotalEstimatedMinutes());
        assertEquals(arc.getId(), updated.getId());
        assertEquals(arc.getOwner(), updated.getOwner());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenArcDoesNotExists() {
        final var arc = anArc();
        final var updateDto = anArcUpdateDto();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(arc.getId(), updateDto))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).save(any(Arc.class));
        verify(arcRepository).findById(arc.getId());
    }

    @Test
    void shouldDeleteArc_whenArcExists() {
        final var arc = anArc();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.of(arc));

        service.delete(arc.getId());

        verify(arcRepository).findById(arc.getId());
        verify(arcRepository).delete(arc);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenArcDoesNotExists() {
        final var arc = anArc();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
        verify(arcRepository).findById(arc.getId());
    }

    @Test
    void shouldDeleteAllArcs_whenUserExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(arcRepository.findAllByOwner(user.getId())).thenReturn(List.of(arc));

        service.deleteAllForUser(user.getId());

        verify(userRepository).existsById(user.getId());
        verify(arcRepository).deleteAll(List.of(arc));
    }

    @Test
    void shouldThrowExceptionOnDeleteAllArcs_whenUserDoesNotExists() {
        final var userId = UserId.random();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteAllForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(arcRepository, never()).deleteAll(anyList());
        verify(userRepository).existsById(userId);
    }

    @Test
    void shouldGetAllArcsForUser_whenUserExists() {
        final var arc = anArc();

        when(userRepository.existsById(any())).thenReturn(true);

        service.findAllForUser(arc.getOwner());

        verify(userRepository).existsById(arc.getOwner());
        verify(arcRepository).findAllByOwner(arc.getOwner());
    }

    @Test
    void shouldThrowExceptionOnGetAllArcsForUser_whenUserDoesNotExists() {
        final var arc = anArc();

        when(userRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.findAllForUser(arc.getOwner()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getOwner().id()));

        verify(userRepository).existsById(arc.getOwner());
        verify(arcRepository, never()).findAllByOwner(any(UserId.class));
    }
}