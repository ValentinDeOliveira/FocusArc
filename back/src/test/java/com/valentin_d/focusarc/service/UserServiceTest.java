package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.EmailAlreadyExistsException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.fixtures.user.UserBuilder;
import com.valentin_d.focusarc.fixtures.user.UserCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.user.UserUpdateDtoBuilder;
import com.valentin_d.focusarc.model.User;
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
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserService service;

    @Test
    void shouldCreateUser_whenEmailDoesNotExist() {
        final var creationDto = UserCreationDtoBuilder.builder().build().build();
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
        final var creationDto = UserCreationDtoBuilder.builder().build().build();
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
        final var user = UserBuilder.builder().build().build();
        final var updateDto = UserUpdateDtoBuilder.builder().build().build();
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final User updated = service.update(updateDto, user.getId());

        verify(repository).save(user);
        verify(repository).findById(user.getId());

        assertEquals(updateDto.name(), updated.getName());
        assertEquals(user.getEmail(), updated.getEmail());
        assertEquals(user.getId(), updated.getId());
    }

    @Test
    void shouldThrowException_whenUpdatingNonExistingUser() {
        final var user = UserBuilder.builder().build().build();
        final var updateDto = UserUpdateDtoBuilder.builder().build().build();
        when(repository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(updateDto, user.getId()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(user.getId().id()));

        verify(repository, never()).save(user);
        verify(repository).findById(user.getId());
    }

    @Test
    void shouldDeleteUser_whenIdExists() {
        final var user = UserBuilder.builder().build().build();
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        service.delete(user.getId());

        verify(repository).findById(user.getId());
        verify(repository).delete(user);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistingUser() {
        final var user = UserBuilder.builder().build().build();
        when(repository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(user.getId()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(user.getId().id()));

        verify(repository, never()).delete(user);
        verify(repository).findById(user.getId());
    }
}