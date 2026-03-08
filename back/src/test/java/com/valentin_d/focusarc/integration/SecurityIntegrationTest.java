package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.filter.JwtAuthFilter;
import com.valentin_d.focusarc.integration.base.BaseIntegrationTest;
import com.valentin_d.focusarc.service.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityIntegrationTest extends BaseIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private JwtService jwtService;

    @TestConfiguration
    static class TestEnforcedSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/**", "/error").permitAll()
                            .anyRequest().authenticated())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        AuthenticationManager authenticationManager() {
            return authentication -> {
                throw new UnsupportedOperationException("Not supported in security integration tests");
            };
        }
    }

    @Test
    void shouldReturnUnauthorized_whenNoTokenProvided() {
        final var response = restTemplate.getForEntity(buildUrl("/arcs/me"), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorized_whenTokenIsInvalid() {
        final var headers = new HttpHeaders();
        headers.setBearerAuth("invalid.jwt.token");

        final var response = restTemplate.exchange(buildUrl("/arcs/me"), HttpMethod.GET,
                new HttpEntity<>(headers), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldPassSecurity_whenTokenIsValid() {
        final var user = domainFixture.user();
        final var headers = new HttpHeaders();
        headers.setBearerAuth(jwtService.generateToken(user));

        final var response = restTemplate.exchange(buildUrl("/arcs/me"), HttpMethod.GET,
                new HttpEntity<>(headers), Void.class);

        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldPermitAccess_whenEndpointIsUnderAuthPath() {
        // No auth header - security should let /auth/** through to the handler.
        // Validation fails on empty body → 400, but NOT 401 (security rejected).
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final var response = restTemplate.exchange(buildUrl("/auth/register"), HttpMethod.POST,
                new HttpEntity<>("{}", headers), Void.class);

        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }
}