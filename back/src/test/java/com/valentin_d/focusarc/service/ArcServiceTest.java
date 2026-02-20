package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.fixtures.arc.ArcBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcUpdateDtoBuilder;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        final var creationDto = ArcCreationDtoBuilder.builder().build().build();
        when(userRepository.existsById(any())).thenReturn(true);

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(creationDto.userId(), result.getOwner());
        assertEquals(creationDto.name(), result.getName());
        assertEquals(0, result.getTotalCompletedMinutes());
        assertEquals(creationDto.totalPlannedMinutes(), result.getTotalPlannedMinutes());

        verify(userRepository).existsById(any(UserId.class));
        verify(arcRepository).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenUserNotFound() {
        final var creationDto = ArcCreationDtoBuilder.builder().build().build();

        when(userRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.userId().id().toString()));

        verify(userRepository).existsById(creationDto.userId());
        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldUpdate_whenArcExists() {
        final var arc = ArcBuilder.builder().build().build();
        final var updateDto = ArcUpdateDtoBuilder.builder().build().build();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.of(arc));

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Arc updated = service.update(arc.getId(), updateDto);

        verify(arcRepository).save(arc);
        verify(arcRepository).findById(arc.getId());

        assertEquals(arc.getName(), updated.getName());
        assertEquals(arc.getTotalPlannedMinutes(), updated.getTotalPlannedMinutes());
        assertEquals(arc.getId(), updated.getId());
        assertEquals(arc.getOwner(), updated.getOwner());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenArcDoesNotExists() {
        final var arc = ArcBuilder.builder().build().build();
        final var updateDto = ArcUpdateDtoBuilder.builder().build().build();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(arc.getId(), updateDto))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).save(any(Arc.class));
        verify(arcRepository).findById(arc.getId());
    }

    @Test
    void shouldDeleteArc_whenArcExists() {
        final var arc = ArcBuilder.builder().build().build();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.of(arc));

        service.delete(arc.getId());

        verify(arcRepository).findById(arc.getId());
        verify(arcRepository).delete(arc);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenArcDoesNotExists() {
        final var arc = ArcBuilder.builder().build().build();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
        verify(arcRepository).findById(arc.getId());
    }

    @Test
    void shouldDeleteAllArcs_whenUserExists() {
        final var arc = ArcBuilder.builder().build().build();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.of(arc));

        service.delete(arc.getId());

        verify(arcRepository).findById(arc.getId());
        verify(arcRepository).delete(arc);
    }

    @Test
    void shouldThrowExceptionOnDeleteAllArcs_whenUserDoesNotExists() {
        final var arc = ArcBuilder.builder().build().build();

        when(arcRepository.findById(arc.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(arc.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
        verify(arcRepository).findById(arc.getId());
    }

    @Test
    void shouldGetAllArcsForUser_whenUserExists() {
        final var arc = ArcBuilder.builder().build().build();

        when(userRepository.existsById(any())).thenReturn(true);

        service.findAllForUser(arc.getOwner());

        verify(userRepository).existsById(arc.getOwner());
        verify(arcRepository).findAllByOwner(arc.getOwner());
    }

    @Test
    void shouldThrowExceptionOnGetAllArcsForUser_whenUserDoesNotExists() {
        final var arc = ArcBuilder.builder().build().build();

        when(userRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.findAllForUser(arc.getOwner()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getOwner().id()));

        verify(userRepository).existsById(arc.getOwner());
        verify(arcRepository, never()).findAllByOwner(any(UserId.class));
    }
}