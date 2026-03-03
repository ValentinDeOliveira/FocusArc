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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArcController.class)
class ArcControllerTest extends BaseControllerTest {
    @MockitoBean
    private ArcService arcService;
    private final static String ROOT = "/arcs";
    private final ArcAssertion arcAssertion = new ArcAssertion();

    @Test
    void shouldReturnArc_whenIdExists() throws Exception {
        final var arc = anArc();
        when(arcService.findById(arc.getId())).thenReturn(Optional.of(arc));

        final var actions = mvcGet(ROOT + "/" + arc.getId().id())
                .andExpect(status().isOk());

        arcAssertion.assertSingleJson(actions, arc);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExists() throws Exception {
        final var arc = anArc();
        when(arcService.findById(arc.getId())).thenReturn(Optional.empty());

        mvcGet(ROOT + "/" + arc.getId().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfArc_whenUserIdExists() throws Exception {
        final var arc = anArc();
        when(arcService.findAllForUser(arc.getOwner())).thenReturn(List.of(arc));

        final var actions = mvcGet(ROOT + "/users/" + arc.getOwner().id())
                .andExpect(status().isOk());

        arcAssertion.assertListJson(actions, arc);
    }

    @Test
    void shouldReturnNoContent_whenUserHasNoArcs() throws Exception {
        when(arcService.findAllForUser(any())).thenReturn(List.of());

        mvcGet(ROOT + "/users/" + ArcId.random().id())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateArc_whenDataIsValid() throws Exception {
        final var arc = anArc();
        final var creationDto = anArcCreationDto();

        when(arcService.create(any(), any())).thenReturn(arc);

        final var json = toJson(creationDto);

        final var actions = mvcPost(ROOT, json)
                .andExpect(status().isCreated());

        arcAssertion.assertSingleJson(actions, arc);
    }

    @Test
    void shouldReturnArc_whenUpdatingExistingArc() throws Exception {
        final var arc = anArc();
        final var updateDto = anArcUpdateDto();

        when(arcService.update(eq(arc.getId()), any())).thenReturn(arc);

        final String json = toJson(updateDto);

        final var actions = mvcPut(ROOT + "/" + arc.getId().id(), json)
                .andExpect(status().isOk());

        arcAssertion.assertSingleJson(actions, arc);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingArc() throws Exception {
        final var arc = anArc();

        mvcDelete(ROOT + "/" + arc.getId().id())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllArcForExistingUser() throws Exception {
        final var arc = anArc();

        mvcDelete(ROOT + "/users/" + arc.getOwner().id())
                .andExpect(status().isNoContent());
    }
}