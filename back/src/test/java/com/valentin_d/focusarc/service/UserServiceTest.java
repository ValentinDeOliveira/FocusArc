package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.EmailAlreadyExistsException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.repository.UserRepository;
import com.valentin_d.focusarc.service.user.UserLoader;
import com.valentin_d.focusarc.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private UserLoader userLoader;
    @InjectMocks
    private UserService service;

    @Test
    void shouldCreateUser_whenEmailDoesNotExist() {
        final var creationDto = aUserCreationDto();
        when(repository.findByEmail(creationDto.email()))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(result.getEmail(), creationDto.email());
        verify(repository).findByEmail(creationDto.email());
        verify(repository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenEmailAlreadyExists() {
        final var creationDto = aUserCreationDto();
        when(repository.findByEmail(creationDto.email()))
                .thenReturn(Optional.of(new User("Existing", creationDto.email())));

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(creationDto.email());

        verify(repository).findByEmail(creationDto.email());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUser_whenIdExists() {
        final var user = aUser();
        final var updateDto = aUserUpdateDto();
        when(userLoader.getUserIfExists(user.getId())).thenReturn(user);
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final User updated = service.update(user.getId(), updateDto);

        verify(repository).save(user);

        assertEquals(updateDto.name(), updated.getName());
        assertEquals(user.getEmail(), updated.getEmail());
        assertEquals(user.getId(), updated.getId());
    }

    @Test
    void shouldThrowException_whenUpdatingNonExistingUser() {
        final var user = aUser();
        final var updateDto = aUserUpdateDto();
        when(userLoader.getUserIfExists(user.getId())).thenThrow(UserDoesNotExistException.class);

        assertThatThrownBy(() -> service.update(user.getId(), updateDto))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(repository, never()).save(user);
    }

    @Test
    void shouldDeleteUser_whenIdExists() {
        final var user = aUser();
        when(userLoader.getUserIfExists(user.getId())).thenReturn(user);

        service.delete(user.getId());

        verify(repository).delete(user);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistingUser() {
        final var user = aUser();
        when(userLoader.getUserIfExists(user.getId())).thenThrow(UserDoesNotExistException.class);

        assertThatThrownBy(() -> service.delete(user.getId()))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(repository, never()).delete(user);
    }
}