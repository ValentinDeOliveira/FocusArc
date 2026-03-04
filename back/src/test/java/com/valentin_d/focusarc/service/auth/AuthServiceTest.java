package com.valentin_d.focusarc.service.auth;

import com.valentin_d.focusarc.service.user.UserLoader;
import com.valentin_d.focusarc.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserLoader userLoader;
    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegister_whenCreationSucess() {

    }
}