package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.UserAssertion;
import com.valentin_d.focusarc.exception.UserDoesNotExistException;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUserUpdateDto;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserService userService;
    private final static String ROOT = "/users";
    private final UserAssertion userAssertion = new UserAssertion();

    @Test
    void shouldReturnUser_whenEmailExists() throws Exception {
        final var user = aUser();
        when(userService.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        final var actions = mvcGet(ROOT + "/email?email=" + user.getEmail())
                .andExpect(status().isOk());

        userAssertion.assertSingleJson(actions, user);
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
    void shouldReturnUser_whenIdExists() throws Exception {
        final var user = aUser();
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));

        final var actions = mvcGet(ROOT + "/" + user.getId().id())
                .andExpect(status().isOk());

        userAssertion.assertSingleJson(actions, user);
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
        when(userService.update(eq(user.getId()), any())).thenReturn(user);

        final var json = toJson(aUserUpdateDto());

        final var actions = mvcPut(ROOT + "/" + user.getId().id(), json)
                .andExpect(status().isOk());

        userAssertion.assertSingleJson(actions, user);
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingUser() throws Exception {
        final var user = aUser();
        when(userService.update(eq(user.getId()), any())).thenThrow(new UserDoesNotExistException(user.getId()));

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
}