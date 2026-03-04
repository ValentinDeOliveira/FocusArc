package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.AuthResponseAssertion;
import com.valentin_d.focusarc.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.aLoginDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthFactory.aRefreshRequestDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.AuthResponseDtoFactory.anAuthResponseDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDto;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {
    @MockitoBean
    private AuthService service;
    private final static String ROOT = "/auth";
    private final AuthResponseAssertion authResponseAssertion = new AuthResponseAssertion();

    @Test
    void shouldCreateUser_whenDataIsValid() throws Exception {
        final var registerRequestDto = aRegisterRequestDto();
        final var authResponse = anAuthResponseDto();
        when(service.register(registerRequestDto)).thenReturn(authResponse);

        final var json = toJson(registerRequestDto);

        final var actions = mvcPost(ROOT + "/register", json)
                .andExpect(status().isCreated());

        authResponseAssertion.assertSingleJson(actions, authResponse);
    }

    @Test
    void shouldLogin_whenDataIsValid() throws Exception {
        final var loginDto = aLoginDto();
        final var authResponse = anAuthResponseDto();

        when(service.login(loginDto)).thenReturn(authResponse);
        final var json = toJson(loginDto);

        final var actions = mvcPost(ROOT + "/login", json)
                .andExpect(status().isOk());

        authResponseAssertion.assertSingleJson(actions, authResponse);
    }

    @Test
    void shouldRefresh_whenDataIsValid() throws Exception {
        final var refreshDto = aRefreshRequestDto();
        final var authResponse = anAuthResponseDto();

        when(service.refresh(refreshDto)).thenReturn(authResponse);
        final var json = toJson(refreshDto);

        final var actions = mvcPost(ROOT + "/refresh", json)
                .andExpect(status().isOk());

        authResponseAssertion.assertSingleJson(actions, authResponse);
    }
}