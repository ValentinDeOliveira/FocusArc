package com.valentin_d.focusarc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

abstract class BaseControllerTest {
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
}