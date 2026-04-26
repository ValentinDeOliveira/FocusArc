package com.valentin_d.focusarc.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.valentin_d.focusarc.exception.auth.InvalidGoogleTokenException;
import com.valentin_d.focusarc.exception.auth.InvalidTokenException;
import com.valentin_d.focusarc.exception.user.InvalidCredentialsException;
import com.valentin_d.focusarc.model.auth.*;
import com.valentin_d.focusarc.model.user.AuthProvider;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.user.UserLoader;
import com.valentin_d.focusarc.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserLoader userLoader;

    @Value("${google.client-id}")
    private String googleClientId;

    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @PostConstruct
    void initGoogleVerifier() {
        googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(googleClientId))
                .build();
    }

    public AuthResponseDto register(final RegisterRequestDto dto) {
        final var user = userService.create(dto);
        return new AuthResponseDto(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthResponseDto login(final LoginRequestDto dto) {
        try {
            userLoader.assertUserNotFromProvider(dto.email(), AuthProvider.GOOGLE);
            final var authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
            final var auth = authenticationManager.authenticate(authToken);
            final var user = (User) auth.getPrincipal();
            return new AuthResponseDto(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

    public AuthResponseDto loginWithGoogle(final GoogleAuthRequestDto dto) {
        final GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(dto.idToken());
        } catch (Exception e) {
            throw new InvalidGoogleTokenException();
        }

        if (idToken == null || !idToken.getPayload().getEmailVerified()) {
            throw new InvalidGoogleTokenException();
        }

        final var payload = idToken.getPayload();
        final var email = payload.getEmail();

        userLoader.assertUserNotFromProvider(email, AuthProvider.LOCAL);

        final var name = (String) payload.get("name");

        final var user = userService.findOrCreateGoogleUser(email, name);
        return new AuthResponseDto(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthResponseDto refresh(final RefreshRequestDto dto) {
        final String email;
        try {
            email = jwtService.extractUsername(dto.refreshToken());
        } catch (Exception e) {
            throw new InvalidTokenException();
        }

        final var user = userLoader.getUserByEmail(email)
                .orElseThrow(InvalidTokenException::new);

        if (!jwtService.isTokenValid(dto.refreshToken(), user)) {
            throw new InvalidTokenException();
        }
        return new AuthResponseDto(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }
}