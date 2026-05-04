package com.valentin_d.focusarc.filter;

import com.valentin_d.focusarc.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @InjectMocks
    private JwtAuthFilter filter;

    private static final String ACCESS_TOKEN = "access_token";
    private static final String VALID_TOKEN = "valid-token";
    private static final String COOKIE_TOKEN = "cookie-token";
    private static final String HEADER_TOKEN = "header-token";
    private static final String INVALID_TOKEN = "invalid-token";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_whenPathStartsWithAuth() {
        final var request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldFilter_whenPathIsProtected() {
        final var request = new MockHttpServletRequest();
        request.setServletPath("/tasks/today");

        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    void shouldPassThrough_whenNoTokenPresent() throws Exception {
        final var request = new MockHttpServletRequest();
        final var response = new MockHttpServletResponse();
        final var chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(any());
    }

    @Test
    void shouldAuthenticate_whenAccessTokenCookiePresent() throws Exception {
        final var user = aUser();
        final var request = new MockHttpServletRequest();
        request.setCookies(new Cookie(ACCESS_TOKEN, VALID_TOKEN));
        final var response = new MockHttpServletResponse();
        final var chain = mock(FilterChain.class);

        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid(VALID_TOKEN, user)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticate_whenBearerHeaderPresent() throws Exception {
        final var user = aUser();
        final var request = getRequestHeader(VALID_TOKEN);
        final var response = new MockHttpServletResponse();
        final var chain = mock(FilterChain.class);

        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid(VALID_TOKEN, user)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldPreferCookie_whenBothCookieAndBearerHeaderArePresent() throws Exception {
        final var user = aUser();
        final var request = getRequestHeader(HEADER_TOKEN);
        request.setCookies(new Cookie(ACCESS_TOKEN, COOKIE_TOKEN));
        final var response = new MockHttpServletResponse();
        final var chain = mock(FilterChain.class);

        when(jwtService.extractUsername(COOKIE_TOKEN)).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid(COOKIE_TOKEN, user)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(jwtService).extractUsername(COOKIE_TOKEN);
        verify(jwtService, never()).extractUsername(HEADER_TOKEN);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticate_whenTokenIsInvalid() throws Exception {
        final var user = aUser();
        final var request = getRequestHeader(INVALID_TOKEN);
        final var response = new MockHttpServletResponse();
        final var chain = mock(FilterChain.class);

        when(jwtService.extractUsername(INVALID_TOKEN)).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid(INVALID_TOKEN, user)).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    private MockHttpServletRequest getRequestHeader(String token) {
        final var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}