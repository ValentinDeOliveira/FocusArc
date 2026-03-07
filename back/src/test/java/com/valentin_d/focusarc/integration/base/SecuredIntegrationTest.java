package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;

@Import(SecuredIntegrationTest.TestSecuredAuthConfig.class)
public abstract class SecuredIntegrationTest extends RestIntegrationTest {
    @Autowired
    private JwtService jwtService;

    protected User user;
    protected HttpHeaders userHeaders;

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

    protected HttpHeaders getHeadersForUser(final User user) {
        final var token = jwtService.generateToken(user);

        final var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }
}