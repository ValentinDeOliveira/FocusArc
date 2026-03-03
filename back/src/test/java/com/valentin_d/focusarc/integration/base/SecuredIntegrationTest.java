package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.filter.JwtAuthFilter;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Import(SecuredIntegrationTest.TestSecurityConfig.class)
public abstract class SecuredIntegrationTest extends RestIntegrationTest {
    @Autowired
    private JwtService jwtService;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(final HttpSecurity http, final JwtAuthFilter jwtAuthFilter) {
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

    protected HttpHeaders getHeadersForUser(final User user) {
        final var token = jwtService.generateToken(user);

        final var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }
}