package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.service.user.UserLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(BaseAuthIntegrationTest.TestAuthConfig.class)
public abstract class BaseAuthIntegrationTest extends RestIntegrationTest {
    protected final String URL = "/auth";
    @Autowired
    protected UserLoader userLoader;

    protected void assertAuthCookiesPresent(final ResponseEntity<?> response) {
        final var cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        assertTrue(cookies.stream().anyMatch(c -> c.startsWith("access_token=")));
        assertTrue(cookies.stream().anyMatch(c -> c.startsWith("refresh_token=")));
    }

    protected String extractCookieValue(final ResponseEntity<?> response, final String name) {
        final var cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies == null) return null;
        return cookies.stream()
                .filter(c -> c.startsWith(name + "="))
                .map(c -> c.split(";")[0].substring(name.length() + 1))
                .findFirst()
                .orElse(null);
    }

    @TestConfiguration
    static class TestAuthConfig {
        @Bean
        public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) {
            return config.getAuthenticationManager();
        }
    }
}