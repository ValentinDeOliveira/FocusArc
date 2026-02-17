package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.exception.EmailAlreadyExistsException;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private static final UserCreationDto dto = new UserCreationDto("Valentin", "test@mail.com");

    @Test
    void should_create_user_when_email_does_not_exist() {
        when(repository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final User result = service.create(dto);

        assertThat(result.getEmail()).isEqualTo(dto.email());
        verify(repository).findByEmail(dto.email());
        verify(repository).save(any(User.class));
    }

    @Test
    void should_throw_exception_when_email_already_exists() {
        when(repository.findByEmail(dto.email()))
                .thenReturn(Optional.of(new User("Existing", dto.email())));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(dto.email());

        verify(repository).findByEmail(dto.email());
        verify(repository, never()).save(any(User.class));
    }
}