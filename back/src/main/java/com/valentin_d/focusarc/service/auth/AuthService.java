package com.valentin_d.focusarc.service.auth;

import com.valentin_d.focusarc.exception.InvalidCredentialsException;
import com.valentin_d.focusarc.exception.InvalidTokenException;
import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import com.valentin_d.focusarc.model.auth.LoginRequestDto;
import com.valentin_d.focusarc.model.auth.RefreshRequestDto;
import com.valentin_d.focusarc.model.auth.RegisterRequestDto;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.user.UserLoader;
import com.valentin_d.focusarc.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserLoader userLoader;

    public AuthResponseDto register(final RegisterRequestDto dto) {
        final var user = userService.create(dto);
        return new AuthResponseDto(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthResponseDto login(final LoginRequestDto dto) {
        try {
            final var authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
            final var auth = authenticationManager.authenticate(authToken);
            final var user = (User) auth.getPrincipal();
            return new AuthResponseDto(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
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