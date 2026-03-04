package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.filter.JwtAuthFilter;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Import(RestIntegrationTest.TestRestSecurityConfig.class)
public abstract class RestIntegrationTest extends BaseIntegrationTest {
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @TestConfiguration
    static class TestRestSecurityConfig {
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
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final Object body, final Class<T> responseType) {
        var requestEntity = new HttpEntity<>(body);
        return restTemplate.exchange(buildUrl(url), method, requestEntity, responseType);
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final Class<T> responseType) {
        return restTemplate.exchange(buildUrl(url), method, HttpEntity.EMPTY, responseType);
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final HttpEntity<?> requestEntity ,final Class<T> responseType) {
        return restTemplate.exchange(buildUrl(url), method, requestEntity, responseType);
    }
}