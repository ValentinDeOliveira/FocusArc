package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.filter.JwtAuthFilter;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(BaseIntegrationTest.TestSecurityConfig.class)
public abstract class BaseIntegrationTest {
    @Autowired
    private JwtService jwtService;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(final HttpSecurity http,
                                                       final JwtAuthFilter jwtAuthFilter) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager() {
            return authentication -> {
                throw new UnsupportedOperationException("Not supported in integration tests");
            };
        }
    }

    @LocalServerPort
    private int port;
    protected final TestRestTemplate restTemplate = new TestRestTemplate();
    protected static final String BASE_URL = "http://localhost:";

    protected String buildUrl(final String path) {
        return BASE_URL + port + path;
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method, final Object body, final Class<T> responseType) {
        var requestEntity = new HttpEntity<>(body);
        return restTemplate.exchange(buildUrl(url), method, requestEntity, responseType);
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method, final Class<T> responseType) {
        return restTemplate.exchange(buildUrl(url), method, HttpEntity.EMPTY, responseType);
    }

    protected <T> void assertNotFound(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.NOT_FOUND, response);
    }

    protected <T> void assertBadRequest(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.BAD_REQUEST, response);
    }

    protected <T> void assertConflict(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.CONFLICT, response);
    }

    protected <T> void assertNoContent(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.NO_CONTENT, response);
    }

    protected <T> void assertOk(final ResponseEntity<T> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private  <T> void assertResponseEmpty(final HttpStatus status,final ResponseEntity<T> response) {
        assertEquals(status, response.getStatusCode());
        assertNull(response.getBody());
    }

    protected  <T> void assertCreated(final ResponseEntity<T> response) {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    protected <T> T expectedValue(T newValue, T originalValue) {
        return newValue != null ? newValue : originalValue;
    }

    protected HttpHeaders getHeadersForUser(final User user) {
        final var token = jwtService.generateToken(user);

        final var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }
}