package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
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

    private static final UserId USER_ID = UserId.random();
    private static final String ARC_NAME = "Arc 1";
    private static final int TOTAL_PLANNED_MINUTES = 120;
    private static final String ARC_NAME_UPDATE = "Arc foobar";
    private static final int TOTAL_PLANNED_MINUTES_UPDATES = 150;

    private static final ArcCreationDto CREATION_DTO = new ArcCreationDto(USER_ID, ARC_NAME, TOTAL_PLANNED_MINUTES);
    private static final ArcUpdateDto UPDATE_DTO = new ArcUpdateDto(ARC_NAME_UPDATE, TOTAL_PLANNED_MINUTES_UPDATES);

    private static final Arc ARC = new Arc(USER_ID, ARC_NAME, TOTAL_PLANNED_MINUTES);

    @Test
    void shouldCreateArc_whenUserDoesExist() {
        when(userRepository.existsById(any())).thenReturn(true);

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Arc result = service.create(CREATION_DTO);

        assertEquals(CREATION_DTO.userId(), result.getOwner());
        assertEquals(CREATION_DTO.name(), result.getName());
        assertEquals(0, result.getTotalCompletedMinutes());
        assertEquals(TOTAL_PLANNED_MINUTES, result.getTotalPlannedMinutes());


        verify(userRepository).existsById(any(UserId.class));
        verify(arcRepository).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenUserNotFound() {
        when(userRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.create(CREATION_DTO))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(CREATION_DTO.userId().id().toString()));

        verify(userRepository).existsById(CREATION_DTO.userId());
        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldUpdate_whenArcExists() {
        when(arcRepository.findById(ARC.getId())).thenReturn(Optional.of(ARC));

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Arc updated = service.update(ARC.getId(), UPDATE_DTO);

        verify(arcRepository).save(ARC);
        verify(arcRepository).findById(ARC.getId());

        assertEquals(ARC_NAME_UPDATE, updated.getName());
        assertEquals(TOTAL_PLANNED_MINUTES_UPDATES, updated.getTotalPlannedMinutes());
        assertEquals(ARC.getId(), updated.getId());
        assertEquals(ARC.getOwner(), updated.getOwner());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenArcDoesNotExists() {
        when(arcRepository.findById(ARC.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(ARC.getId(), UPDATE_DTO))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(ARC.getId().id()));

        verify(arcRepository, never()).save(any(Arc.class));
        verify(arcRepository).findById(ARC.getId());
    }

    @Test
    void shouldDeleteArc_whenArcExists() {
        when(arcRepository.findById(ARC.getId())).thenReturn(Optional.of(ARC));

        service.delete(ARC.getId());

        verify(arcRepository).findById(ARC.getId());
        verify(arcRepository).delete(ARC);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenArcDoesNotExists() {
        when(arcRepository.findById(ARC.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ARC.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(ARC.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
        verify(arcRepository).findById(ARC.getId());
    }

    @Test
    void shouldDeleteAllArcs_whenUserExists() {
        when(arcRepository.findById(ARC.getId())).thenReturn(Optional.of(ARC));

        service.delete(ARC.getId());

        verify(arcRepository).findById(ARC.getId());
        verify(arcRepository).delete(ARC);
    }

    @Test
    void shouldThrowExceptionOnDeleteAllArcs_whenUserDoesNotExists() {
        when(arcRepository.findById(ARC.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ARC.getId()))
                .isInstanceOf(ArcDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(ARC.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
        verify(arcRepository).findById(ARC.getId());
    }

    @Test
    void shouldGetAllArcsForUser_whenUserExists() {
        when(userRepository.existsById(any())).thenReturn(true);

        service.findAllForUser(USER_ID);

        verify(userRepository).existsById(USER_ID);
        verify(arcRepository).findAllByOwner(USER_ID);
    }

    @Test
    void shouldThrowExceptionOnGetAllArcsForUser_whenUserDoesNotExists() {
        when(userRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.findAllForUser(USER_ID))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(USER_ID.id()));

        verify(userRepository).existsById(USER_ID);
        verify(arcRepository, never()).findAllByOwner(any(UserId.class));
    }
}