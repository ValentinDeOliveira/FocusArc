package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserService userService;

    private final static String ROOT = "/users";

    @Test
    void shouldReturnUser_whenEmailExists() throws Exception {
        final var user = aUser();
        when(userService.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        final var actions = mvcGet(ROOT + "/email?email=" + user.getEmail())
                .andExpect(status().isOk());

        assertUserJson(actions, user);
    }

    @Test
    void shouldReturnNotFound_whenEmailDoesNotExist() throws Exception {
        final var user = aUser();
        when(userService.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        mvcGet(ROOT + "/email?email=" + user.getEmail())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUser_whenDataIsValid() throws Exception {
        final var user = aUser();
        when(userService.create(any())).thenReturn(user);

        final var json = toJson(aUserCreationDto());

        final var actions = mvcPost(ROOT, json)
                .andExpect(status().isCreated());

        assertUserJson(actions, user);
    }

    @Test
    void shouldReturnUser_whenIdExists() throws Exception {
        final var user = aUser();
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));

        final var actions = mvcGet(ROOT + "/" + user.getId().id())
                .andExpect(status().isOk());

        assertUserJson(actions, user);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        when(userService.findById(any(UserId.class))).thenReturn(Optional.empty());

        mvcGet(ROOT + "/" + UserId.random().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUser_whenIdExists() throws Exception {
        final var user = aUser();
        when(userService.update(any(), eq(user.getId()))).thenReturn(user);

        final var json = toJson(aUserUpdateDto());

        final var actions = mvcPut(ROOT + "/" + user.getId().id(), json)
                .andExpect(status().isOk());

        assertUserJson(actions, user);
        actions.andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingUser() throws Exception {
        final var user = aUser();
        when(userService.update(any(), eq(user.getId()))).thenThrow(new UserDoesNotExistException(user.getId()));

        mvcPut(ROOT + "/" + user.getId().id(), toJson(aUserUpdateDto()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUser_whenIdExists() throws Exception {
        final var user = aUser();
        mvcDelete(ROOT + "/" + user.getId().id())
                .andExpect(status().isNoContent());

        verify(userService).delete(user.getId());
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingUser() throws Exception {
        final var user = aUser();
        doThrow(new UserDoesNotExistException(user.getId())).when(userService).delete(user.getId());

        mvcDelete(ROOT + "/" + user.getId().id())
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