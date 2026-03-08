package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.ArcAssertion;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.service.arc.ArcService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArcController.class)
class ArcControllerTest extends BaseSecurityControllerTest {
    @MockitoBean
    private ArcService arcService;
    private final static String ROOT = "/arcs";
    private final ArcAssertion arcAssertion = new ArcAssertion();

    @Test
    void shouldReturnArc_whenIdExists() throws Exception {
        final var arc = anArcWithOwnerId(user.getId());
        when(arcService.findByIdAndOwnerId(eq(arc.getId()), eq(user.getId())))
                .thenReturn(Optional.of(arc));

        final var actions = mvcGetWithUser(arcUrl(arc.getId()), user)
                .andExpect(status().isOk());

        arcAssertion.assertSingleJson(actions, arc);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExists() throws Exception {
        when(arcService.findByIdAndOwnerId(any(ArcId.class), eq(user.getId())))
                .thenReturn(Optional.empty());

        mvcGetWithUser(arcUrl(ArcId.random()), user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfArc_whenUserIdExists() throws Exception {
        final var arc = anArcWithOwnerId(user.getId());

        when(arcService.findAllForUser(user.getId())).thenReturn(List.of(arc));

        final var actions = mvcGetWithUser(ROOT + "/me", user)
                .andExpect(status().isOk());

        arcAssertion.assertListJson(actions, arc);
    }

    @Test
    void shouldReturnNoContent_whenUserHasNoArcs() throws Exception {
        when(arcService.findAllForUser(any())).thenReturn(List.of());

        mvcGetWithUser(ROOT + "/me", aUser())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateArc_whenDataIsValid() throws Exception {
        final var arc = anArcWithOwnerId(user.getId());
        final var creationDto = anArcCreationDto();

        when(arcService.create(eq(user.getId()), eq(creationDto))).thenReturn(arc);

        final var json = toJson(creationDto);

        final var actions = mvcPostWithUser(ROOT, json, user)
                .andExpect(status().isCreated());

        arcAssertion.assertSingleJson(actions, arc);
    }

    @Test
    void shouldReturnArc_whenUpdatingExistingArc() throws Exception {
        final var arc = anArcWithOwnerId(user.getId());
        final var updateDto = anArcUpdateDto();

        when(arcService.update(eq(user.getId()), eq(arc.getId()), eq(updateDto))).thenReturn(arc);

        final String json = toJson(updateDto);

        final var actions = mvcPutWithUser(arcUrl(arc.getId()), json, user)
                .andExpect(status().isOk());

        arcAssertion.assertSingleJson(actions, arc);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingArc() throws Exception {
        final var arc = anArcWithOwnerId(user.getId());

        mvcDeleteWithUser(arcUrl(arc.getId()), user)
                .andExpect(status().isNoContent());

        verify(arcService).delete(eq(user.getId()), eq(arc.getId()));
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllArcForExistingUser() throws Exception {
        mvcDeleteWithUser(ROOT, user)
                .andExpect(status().isNoContent());

        verify(arcService).deleteAllForUser(eq(user.getId()));
    }

    private String arcUrl(ArcId arcId) {
        return ROOT + "/" + arcId.id();
    }
}