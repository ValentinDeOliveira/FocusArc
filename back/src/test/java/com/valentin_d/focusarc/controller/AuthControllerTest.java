package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.valentin_d.focusarc.fixtures.factory.auth.AuthResponseDtoFactory.anAuthResponseDto;
import static com.valentin_d.focusarc.fixtures.factory.auth.RegisterRequestDtoFactory.aRegisterRequestDto;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {
    @MockitoBean
    private AuthService service;
    private final static String ROOT = "/auth";

    @Test
    void shouldCreateUser_whenDataIsValid() throws Exception {
        final var registerRequestDto = aRegisterRequestDto();
        final var authResponse = anAuthResponseDto();
        when(service.register(registerRequestDto)).thenReturn(authResponse);

        final var json = toJson(registerRequestDto);

        final var actions = mvcPost(ROOT + "/register", json)
                .andExpect(status().isCreated());

        actions
                .andExpect(jsonPath("$.accessToken").value(authResponse.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.refreshToken()));
    }
}