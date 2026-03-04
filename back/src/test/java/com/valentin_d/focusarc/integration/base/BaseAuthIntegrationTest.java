package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.service.user.UserLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Import(BaseAuthIntegrationTest.TestAuthConfig.class)
public abstract class BaseAuthIntegrationTest extends RestIntegrationTest {
    protected final String URL = "/auth";
    @Autowired
    protected UserLoader userLoader;

    @TestConfiguration
    static class TestAuthConfig {
        @Bean
        public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) {
            return config.getAuthenticationManager();
        }
    }
}