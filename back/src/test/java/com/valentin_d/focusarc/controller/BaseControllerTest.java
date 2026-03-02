package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

abstract class BaseControllerTest {
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    protected <T> String toJson(T object) {
        return objectMapper.writeValueAsString(object);
    }

    protected ResultActions mvcPut(final String url, final String json) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected ResultActions mvcDelete(final String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    protected ResultActions mvcPost(final String url, final String json) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected ResultActions mvcGet(final String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    protected ResultActions mvcGetWith(final String url, final UserDetails user) throws Exception {
        return mockMvc.perform(get(url).with(user(user)));
    }
}