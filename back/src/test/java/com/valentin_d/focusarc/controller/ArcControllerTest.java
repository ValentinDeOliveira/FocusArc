package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.fixtures.arc.ArcBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcUpdateDtoBuilder;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.service.ArcService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArcController.class)
class ArcControllerTest extends BaseControllerTest {
    @MockitoBean
    private ArcService arcService;

    private final static String ROOT = "/arcs";

    @Test
    void shouldReturnArc_whenIdExists() throws Exception {
        final var arc = ArcBuilder.builder().build().build();
        when(arcService.findById(arc.getId())).thenReturn(Optional.of(arc));

        final var actions = mvcGet(ROOT + "/" + arc.getId().id())
                .andExpect(status().isOk());

        assertArcJson(actions, arc);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExists() throws Exception {
        final var arc = ArcBuilder.builder().build().build();
        when(arcService.findById(arc.getId())).thenReturn(Optional.empty());

        mvcGet(ROOT + "/" + arc.getId().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfArc_whenUserIdExists() throws Exception {
        final var arc = ArcBuilder.builder().build().build();
        when(arcService.findAllForUser(arc.getOwner())).thenReturn(List.of(arc));

        final var actions = mvcGet(ROOT + "/users/" + arc.getOwner().id())
                .andExpect(status().isOk());

        assertArcListJson(actions, arc);
    }

    @Test
    void shouldReturnNotFound_whenUserIdDoesNotExists() throws Exception {
        final var arc = ArcBuilder.builder().build().build();
        when(arcService.findAllForUser(arc.getOwner())).thenReturn(List.of());

        mvcGet(ROOT + "/users/" + arc.getOwner().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUser_whenDataIsValid() throws Exception {
        final var arc = ArcBuilder.builder().build().build();
        final var creationDto = ArcCreationDtoBuilder.builder().build().build();

        when(arcService.create(any())).thenReturn(arc);

        final var json = toJson(creationDto);

        final var actions = mvcPost(ROOT, json)
                .andExpect(status().isCreated());

        assertArcJson(actions, arc);
    }

    @Test
    void shouldReturnArc_whenUpdatingExistingArc() throws Exception {
        final var arc = ArcBuilder.builder().build().build();
        final var updateDto = ArcUpdateDtoBuilder.builder().build().build();

        when(arcService.update(eq(arc.getId()), any())).thenReturn(arc);

        final String json = toJson(updateDto);

        final var actions = mvcPut(ROOT + "/" + arc.getId().id(), json)
                .andExpect(status().isOk());

        assertArcJson(actions, arc);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingArc() throws Exception {
        final var arc = ArcBuilder.builder().build().build();

        mvcDelete(ROOT + "/" + arc.getId().id())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllArcForExistingUser() throws Exception {
        final var arc = ArcBuilder.builder().build().build();

        mvcDelete(ROOT + "/users/" + arc.getOwner().id())
                .andExpect(status().isNoContent());
    }

    private void assertArcJson(final ResultActions actions, final Arc expected) throws Exception {
        assertArcJson(actions, "$", expected);
    }

    private void assertArcListJson(final ResultActions actions, final Arc expected) throws Exception {
        assertArcJson(actions, "$[0]", expected);
    }

    private void assertArcJson(final ResultActions actions, final String path, final Arc expected) throws Exception {
        actions
                .andExpect(jsonPath(path + ".id").value(expected.getId().id().toString()))
                .andExpect(jsonPath(path + ".owner").value(expected.getOwner().id().toString()))
                .andExpect(jsonPath(path + ".name").value(expected.getName()))
                .andExpect(jsonPath(path + ".totalPlannedMinutes").value(expected.getTotalPlannedMinutes()))
                .andExpect(jsonPath(path + ".totalCompletedMinutes").value(expected.getTotalCompletedMinutes()));
    }
}