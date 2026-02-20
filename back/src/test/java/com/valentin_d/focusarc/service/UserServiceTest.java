package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.exception.EmailAlreadyExistsException;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
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

    private static final String USERNAME = "foobar";
    private static final String EMAIL = "test@mail.com";
    private static final String USERNAME_UPDATE = "foo";


    private static final UserCreationDto CREATION_DTO = new UserCreationDto(USERNAME, EMAIL);
    private static final UserUpdateDto UPDATE_DTO = new UserUpdateDto(USERNAME_UPDATE);

    private static final User USER = new User(USERNAME, EMAIL);

    @Test
    void shouldCreateUser_whenEmailDoesNotExist() {
        when(repository.findByEmail(CREATION_DTO.email()))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final User result = service.create(CREATION_DTO);

        assertEquals(result.getEmail(), CREATION_DTO.email());
        verify(repository).findByEmail(CREATION_DTO.email());
        verify(repository).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        when(repository.findByEmail(CREATION_DTO.email()))
                .thenReturn(Optional.of(new User("Existing", CREATION_DTO.email())));

        assertThatThrownBy(() -> service.create(CREATION_DTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(CREATION_DTO.email());

        verify(repository).findByEmail(CREATION_DTO.email());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUser_whenIdExists() {
        when(repository.findById(USER.getId())).thenReturn(Optional.of(USER));
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final User updated = service.update(UPDATE_DTO, USER.getId());

        verify(repository).save(USER);
        verify(repository).findById(USER.getId());

        assertEquals(USERNAME_UPDATE, updated.getName());
        assertEquals(USER.getEmail(), updated.getEmail());
        assertEquals(USER.getId(), updated.getId());
    }

    @Test
    void shouldThrowException_whenUpdatingNonExistingUser() {
        when(repository.findById(USER.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(UPDATE_DTO, USER.getId()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(USER.getId().id()));

        verify(repository, never()).save(USER);
        verify(repository).findById(USER.getId());
    }

    @Test
    void shouldDeleteUser_whenIdExists() {
        when(repository.findById(USER.getId())).thenReturn(Optional.of(USER));

        service.delete(USER.getId());

        verify(repository).findById(USER.getId());
        verify(repository).delete(USER);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistingUser() {
        when(repository.findById(USER.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(USER.getId()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(USER.getId().id()));

        verify(repository, never()).delete(USER);
        verify(repository).findById(USER.getId());
    }
}