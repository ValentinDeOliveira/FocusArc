package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

@Import(SecuredIntegrationTest.TestSecuredAuthConfig.class)
public abstract class SecuredIntegrationTest extends RestIntegrationTest {
    protected User user;

    @Autowired
    private JwtService jwtService;
    private HttpHeaders userHeaders;

    @BeforeEach
    void setupUser() {
        user = domainFixture.user();
        userHeaders = getHeadersForUser(user);
    }

    @TestConfiguration
    static class TestSecuredAuthConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return authentication -> {
                throw new UnsupportedOperationException("Not supported in integration tests");
            };
        }
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final Class<T> responseType) {
        return request(url, method, new HttpEntity<>(userHeaders), responseType);
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final Object dto, final Class<T> responseType) {
        return request(url, method, new HttpEntity<>(dto, userHeaders), responseType);
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                             final User user, final Class<T> responseType) {
        return request(url, method, new HttpEntity<>(getHeadersForUser(user)), responseType);
    }

    protected  <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                             final User user, final Object dto, final Class<T> responseType) {
        return request(url, method, new HttpEntity<>(dto, getHeadersForUser(user)), responseType);
    }

    private HttpHeaders getHeadersForUser(final User user) {
        final var token = jwtService.generateToken(user);

        final var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }
}