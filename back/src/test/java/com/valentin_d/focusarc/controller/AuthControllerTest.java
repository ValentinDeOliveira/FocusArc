package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.service.auth.AuthService;
import com.valentin_d.focusarc.service.auth.CookieService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.aGoogleAuthRequestDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.aLoginDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthResponseDtoFactory.anAuthResponseDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseSecurityControllerTest {
    @MockitoBean
    private AuthService service;
    @MockitoBean
    CookieService cookieService;
    private final static String ROOT = "/auth";

    @Test
    void shouldCreateUser_whenDataIsValid() throws Exception {
        final var registerRequestDto = aRegisterRequestDto();
        final var authResponse = anAuthResponseDto();
        when(service.register(registerRequestDto)).thenReturn(authResponse);

        mvcPostWithUser(ROOT + "/register", toJson(registerRequestDto), user)
                .andExpect(status().isCreated());

        verify(cookieService).setAuthCookies(any(), eq(authResponse));
    }

    @Test
    void shouldLogin_whenDataIsValid() throws Exception {
        final var loginDto = aLoginDto();
        final var authResponse = anAuthResponseDto();
        when(service.login(loginDto)).thenReturn(authResponse);

        mvcPostWithUser(ROOT + "/login", toJson(loginDto), user)
                .andExpect(status().isOk());

        verify(cookieService).setAuthCookies(any(), eq(authResponse));
    }

    @Test
    void shouldRefresh_whenDataIsValid() throws Exception {
        final var authResponse = anAuthResponseDto();
        when(cookieService.extractCookieValue(any(), eq(CookieService.REFRESH_TOKEN))).thenReturn("some-token");
        when(service.refresh(any())).thenReturn(authResponse);

        mvcPostWithUser(ROOT + "/refresh", "", user)
                .andExpect(status().isOk());

        verify(cookieService).setAuthCookies(any(), eq(authResponse));
    }

    @Test
    void shouldReturn401_whenRefreshTokenIsAbsent() throws Exception {
        when(cookieService.extractCookieValue(any(), eq(CookieService.REFRESH_TOKEN))).thenReturn(null);

        mvcPostWithUser(ROOT + "/refresh", "", user)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLoginWithGoogle_whenTokenIsValid() throws Exception {
        final var googleDto = aGoogleAuthRequestDto();
        final var authResponse = anAuthResponseDto();
        when(service.loginWithGoogle(googleDto)).thenReturn(authResponse);

        mvcPostWithUser(ROOT + "/google", toJson(googleDto), user)
                .andExpect(status().isOk());

        verify(cookieService).setAuthCookies(any(), eq(authResponse));
    }
}