package com.valentin_d.focusarc.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.valentin_d.focusarc.exception.auth.InvalidGoogleTokenException;
import com.valentin_d.focusarc.exception.auth.InvalidTokenException;
import com.valentin_d.focusarc.exception.user.AccountAlreadyExistsWithProviderException;
import com.valentin_d.focusarc.exception.user.InvalidCredentialsException;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;
import com.valentin_d.focusarc.model.user.AuthProvider;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.user.UserLoader;
import com.valentin_d.focusarc.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.GeneralSecurityException;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDto;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void injectGoogleVerifier() {
        ReflectionTestUtils.setField(authService, "googleIdTokenVerifier", googleIdTokenVerifier);
    }

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
    void shouldThrowExceptionOnLogin_whenAccountAlreadyExistsOnGoogle() {
        final var dto = aLoginDto();

        doThrow(new AccountAlreadyExistsWithProviderException(dto.email(), AuthProvider.GOOGLE))
                .when(userLoader).assertUserNotFromProvider(dto.email(), AuthProvider.GOOGLE);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(AccountAlreadyExistsWithProviderException.class);
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

    @Test
    void shouldLoginWithGoogle_whenTokenIsValid() throws Exception {
        final var dto = aGoogleAuthRequestDto();
        final var user = aUser();
        final var idToken = mock(GoogleIdToken.class);
        final var payload = mock(GoogleIdToken.Payload.class);

        when(googleIdTokenVerifier.verify(dto.idToken())).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getEmailVerified()).thenReturn(true);
        when(payload.getEmail()).thenReturn(user.getEmail());
        when(payload.get("name")).thenReturn("Test User");
        when(userService.findOrCreateGoogleUser(user.getEmail(), "Test User")).thenReturn(user);

        authService.loginWithGoogle(dto);

        verify(userService).findOrCreateGoogleUser(user.getEmail(), "Test User");
        verifyGenerateTokensCalled(user);
    }

    @Test
    void shouldThrowInvalidGoogleTokenException_whenVerifierThrowsException() throws Exception {
        final var dto = aGoogleAuthRequestDto();
        when(googleIdTokenVerifier.verify(dto.idToken())).thenThrow(GeneralSecurityException.class);

        assertThatThrownBy(() -> authService.loginWithGoogle(dto))
                .isInstanceOf(InvalidGoogleTokenException.class);
    }

    @Test
    void shouldThrowInvalidGoogleTokenExceptionOnLoginWithGoogle_whenTokenIsNull() throws Exception {
        final var dto = aGoogleAuthRequestDto();
        when(googleIdTokenVerifier.verify(dto.idToken())).thenReturn(null);

        assertThatThrownBy(() -> authService.loginWithGoogle(dto))
                .isInstanceOf(InvalidGoogleTokenException.class);
    }

    @Test
    void shouldThrowAccountAlreadyExistsWithProviderExceptionOnLoginWithGoogle_whenUserHasLocalAccount() throws Exception {
        final var dto = aGoogleAuthRequestDto();
        final var user = aUser();
        final var idToken = mock(GoogleIdToken.class);
        final var payload = mock(GoogleIdToken.Payload.class);

        when(googleIdTokenVerifier.verify(dto.idToken())).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getEmailVerified()).thenReturn(true);
        when(payload.getEmail()).thenReturn(user.getEmail());
        doThrow(new AccountAlreadyExistsWithProviderException(user.getEmail(), AuthProvider.LOCAL))
                .when(userLoader).assertUserNotFromProvider(user.getEmail(), AuthProvider.LOCAL);

        assertThatThrownBy(() -> authService.loginWithGoogle(dto))
                .isInstanceOf(AccountAlreadyExistsWithProviderException.class);
    }

    @Test
    void shouldThrowInvalidGoogleTokenException_whenEmailNotVerified() throws Exception {
        final var dto = aGoogleAuthRequestDto();
        final var idToken = mock(GoogleIdToken.class);
        final var payload = mock(GoogleIdToken.Payload.class);

        when(googleIdTokenVerifier.verify(dto.idToken())).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getEmailVerified()).thenReturn(false);

        assertThatThrownBy(() -> authService.loginWithGoogle(dto))
                .isInstanceOf(InvalidGoogleTokenException.class);
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