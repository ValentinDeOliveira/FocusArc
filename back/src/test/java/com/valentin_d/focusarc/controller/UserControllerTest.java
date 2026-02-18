package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.exception.UserDoesNotExist;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private static final User USER = new User(NAME, EMAIL);

    @Test
    void getUserByEmail_found() throws Exception {
        when(userService.findByEmail(EMAIL))
                .thenReturn(Optional.of(USER));

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
        when(userService.create(any())).thenReturn(USER);

        final var actions = mockMvc.perform(post(ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + NAME + "\",\"email\":\"" + EMAIL + "\"}"))
                .andExpect(status().isCreated());

        assertUserJson(actions);
    }

    @Test
    void getUserById_found() throws Exception {
        when(userService.findById(USER.getId())).thenReturn(Optional.of(USER));

        final var actions = mockMvc.perform(get(ROOT + "/" + USER.getId().id()))
                .andExpect(status().isOk());

        assertUserJson(actions);
    }

    @Test
    void getUserById_notFound() throws Exception {
        final var id = UserId.random();
        when(userService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get(ROOT + "/" + id.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_returnsOk() throws Exception {
        when(userService.update(any(), eq(USER.getId()))).thenReturn(USER);

        final var actions = mockMvc.perform(put(ROOT + "/" + USER.getId().id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + NAME + "\"}"))
                .andExpect(status().isOk());

        assertUserJson(actions);
        actions.andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.id.id").exists());
    }

    @Test
    void updateUser_notFound() throws Exception {
        when(userService.update(any(), eq(USER.getId()))).thenThrow(new UserDoesNotExist(USER.getId()));

        mockMvc.perform(put(ROOT + "/" + USER.getId().id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        mockMvc.perform(delete(ROOT + "/" + USER.getId().id()))
                .andExpect(status().isNoContent());

        verify(userService).delete(USER.getId());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        doThrow(new UserDoesNotExist(USER.getId())).when(userService).delete(USER.getId());

        mockMvc.perform(delete(ROOT + "/" + USER.getId().id()))
                .andExpect(status().isNotFound());
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