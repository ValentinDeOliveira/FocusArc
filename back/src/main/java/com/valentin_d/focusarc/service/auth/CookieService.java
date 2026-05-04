package com.valentin_d.focusarc.service.auth;

import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${jwt.expiration-time}")
    private long expirationTime;
    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public void setAuthCookies(final HttpServletResponse response, final AuthResponseDto tokens) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(ACCESS_TOKEN, tokens.accessToken(), expirationTime / 1000).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(REFRESH_TOKEN, tokens.refreshToken(), refreshExpirationTime / 1000).toString());
    }

    public void clearAuthCookies(final HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(ACCESS_TOKEN, "", 0).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(REFRESH_TOKEN, "", 0).toString());
    }

    public String extractCookieValue(final HttpServletRequest request, final String name) {
        final var cookies = request.getCookies();
        if (cookies == null) return null;
        for (final var cookie : cookies) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private ResponseCookie buildCookie(final String name, final String value, final long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
    }
}