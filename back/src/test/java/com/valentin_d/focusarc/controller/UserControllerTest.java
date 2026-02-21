package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.fixtures.user.UserBuilder;
import com.valentin_d.focusarc.fixtures.user.UserCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.user.UserUpdateDtoBuilder;
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
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    private final static String ROOT = "/users";

    @Test
    void shouldReturnUser_whenEmailExists() throws Exception {
        final var user = UserBuilder.builder().build().build();
        when(userService.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        final var actions = mockMvc.perform(get(ROOT + "/email").param("email", user.getEmail()))
                .andExpect(status().isOk());

        assertUserJson(actions, user);
    }

    @Test
    void shouldReturnNotFound_whenEmailDoesNotExist() throws Exception {
        final var user = UserBuilder.builder().build().build();
        when(userService.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(ROOT + "/email").param("email", user.getEmail()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUser_whenDataIsValid() throws Exception {
        final var user = UserBuilder.builder().build().build();
        when(userService.create(any())).thenReturn(user);

        final var json = objectMapper.writeValueAsString(
                UserCreationDtoBuilder.builder().build().build()
        );

        final var actions = mockMvc.perform(post(ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        assertUserJson(actions, user);
    }

    @Test
    void shouldReturnUser_whenIdExists() throws Exception {
        final var user = UserBuilder.builder().build().build();
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));

        final var actions = mockMvc.perform(get(ROOT + "/" + user.getId().id()))
                .andExpect(status().isOk());

        assertUserJson(actions, user);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        when(userService.findById(any(UserId.class))).thenReturn(Optional.empty());

        mockMvc.perform(get(ROOT + "/" + UserId.random().id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUser_whenIdExists() throws Exception {
        final var user = UserBuilder.builder().build().build();
        when(userService.update(any(), eq(user.getId()))).thenReturn(user);

        final var json = objectMapper.writeValueAsString(
                UserUpdateDtoBuilder.builder().build().build()
        );

        final var actions = mockMvc.perform(put(ROOT + "/" + user.getId().id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        assertUserJson(actions, user);
        actions.andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingUser() throws Exception {
        final var user = UserBuilder.builder().build().build();
        when(userService.update(any(), eq(user.getId()))).thenThrow(new UserDoesNotExistException(user.getId()));

        mockMvc.perform(put(ROOT + "/" + user.getId().id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUser_whenIdExists() throws Exception {
        final var user = UserBuilder.builder().build().build();
        mockMvc.perform(delete(ROOT + "/" + user.getId().id()))
                .andExpect(status().isNoContent());

        verify(userService).delete(user.getId());
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingUser() throws Exception {
        final var user = UserBuilder.builder().build().build();
        doThrow(new UserDoesNotExistException(user.getId())).when(userService).delete(user.getId());

        mockMvc.perform(delete(ROOT + "/" + user.getId().id()))
                .andExpect(status().isNotFound());
    }


    private void assertUserJson(final ResultActions actions, final User expected) throws Exception {
        actions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()))
                .andExpect(jsonPath("$.id").value(expected.getId().id().toString()))
                .andExpect(jsonPath("$.lastLogin").exists());
    }
}