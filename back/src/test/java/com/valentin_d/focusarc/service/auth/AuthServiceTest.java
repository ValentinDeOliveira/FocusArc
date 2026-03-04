package com.valentin_d.focusarc.service.auth;

import com.valentin_d.focusarc.exception.InvalidCredentialsException;
import com.valentin_d.focusarc.exception.InvalidTokenException;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.user.UserLoader;
import com.valentin_d.focusarc.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.aLoginDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.aRefreshRequestDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDto;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;
    @Mock
    private UserLoader userLoader;
    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegister_whenCreationSucess() {
        final var user = aUser();
        final var dto = aRegisterRequestDto();
        when(userService.create(dto)).thenReturn(user);

        authService.register(dto);

        verify(userService).create(dto);
        verifyGenerateTokensCalled(user);
    }

    @Test
    void shouldLogin_whenCredentialsAreValid() {
        final var user = aUser();
        final var dto = aLoginDto();
        final var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        authService.login(dto);

        verifyGenerateTokensCalled(user);
    }

    @Test
    void shouldThrowInvalidCredentialsException_whenBadCredentials() {
        final var dto = aLoginDto();
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void shouldRefresh_whenTokenIsValid() {
        final var user = aUser();
        final var dto = aRefreshRequestDto();
        when(jwtService.extractUsername(dto.refreshToken())).thenReturn(user.getEmail());
        when(userLoader.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(dto.refreshToken(), user)).thenReturn(true);

        authService.refresh(dto);
        verifyGenerateTokensCalled(user);
    }

    @Test
    void shouldThrowInvalidTokenException_whenExtractUsernameFails() {
        final var dto = aRefreshRequestDto();
        when(jwtService.extractUsername(dto.refreshToken())).thenThrow(RuntimeException.class);

        assertRefreshInvalid(dto);
    }

    @Test
    void shouldThrowInvalidTokenException_whenUserNotFound() {
        final var dto = aRefreshRequestDto();
        when(jwtService.extractUsername(dto.refreshToken())).thenReturn("unknown@example.com");
        when(userLoader.getUserByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertRefreshInvalid(dto);
    }

    @Test
    void shouldThrowInvalidTokenException_whenTokenIsInvalid() {
        final var user = aUser();
        final var dto = aRefreshRequestDto();
        when(jwtService.extractUsername(dto.refreshToken())).thenReturn(user.getEmail());
        when(userLoader.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(dto.refreshToken(), user)).thenReturn(false);

        assertRefreshInvalid(dto);
    }

    private void verifyGenerateTokensCalled(final User user) {
        verify(jwtService).generateToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    private void assertRefreshInvalid(final RefreshRequestDto dto) {
        assertThatThrownBy(() -> authService.refresh(dto))
                .isInstanceOf(InvalidTokenException.class);
    }
}