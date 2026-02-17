package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    private final static String ROOT = "/users";
    private final static String EMAIL = "test@mail.com";
    private final static String NAME = "foobar";

    @Test
    void getUserByEmail_found() throws Exception {
        when(userService.findByEmail(EMAIL))
                .thenReturn(Optional.of(new User(NAME, EMAIL)));

        final var actions = mockMvc.perform(get(ROOT + "/email").param("email", EMAIL))
                .andExpect(status().isOk());

        assertUserJson(actions);
    }

    @Test
    void getUserByEmail_notFound() throws Exception {
        when(userService.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(ROOT + "/email").param("email", EMAIL))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_returnsCreated() throws Exception {
        when(userService.create(any())).thenReturn(new User(NAME, EMAIL));

        final var actions = mockMvc.perform(post(ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + NAME + "\",\"email\":\"" + EMAIL + "\"}"))
                .andExpect(status().isCreated());

        assertUserJson(actions);
    }

    private void assertUserJson(ResultActions actions) throws Exception {
        actions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.id.id").exists())
                .andExpect(jsonPath("$.lastLogin").exists());
    }
}