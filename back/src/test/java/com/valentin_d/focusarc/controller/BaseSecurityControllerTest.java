package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.auth.JwtService;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@Import(TestSecurityConfig.class)
abstract class BaseSecurityControllerTest extends BaseControllerTest {
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    protected ResultActions mvcGetWithUser(final String url, final User user) throws Exception {
        return mvcGet(url, getAuth(user));
    }

    protected ResultActions mvcPutWithUser(final String url, final String json, final User user) throws Exception {
        return mvcPut(url, json, getAuth(user));
    }

    protected ResultActions mvcDeleteWithUser(final String url, final User user) throws Exception {
        return mvcDelete(url, getAuth(user));
    }

    protected ResultActions mvcPostWithUser(final String url, final String json, final User user) throws Exception {
        return mvcPost(url, json, getAuth(user));
    }

    protected ResultActions mvcPatchWithUser(final String url, final String json, final User user) throws Exception {
        return mvcPatch(url, json, getAuth(user));
    }

    private RequestPostProcessor getAuth(final User user) {
        return authentication(new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities()
        ));
    }
}