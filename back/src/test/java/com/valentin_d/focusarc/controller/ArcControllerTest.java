package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.ArcService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArcController.class)
class ArcControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ArcService arcService;

    private final static String ROOT = "/arcs";

    private static final UserId USER_ID = UserId.random();
    private static final ArcId ARC_ID = ArcId.random();
    private static final String ARC_NAME = "Arc 1";
    private static final int TOTAL_PLANNED_MINUTES = 120;

    private static final Arc ARC = new Arc(ARC_ID, USER_ID, ARC_NAME, TOTAL_PLANNED_MINUTES);

    private static final ArcCreationDto CREATION_DTO = new ArcCreationDto(USER_ID, ARC_NAME, TOTAL_PLANNED_MINUTES);
    private static final ArcUpdateDto UPDATE_DTO = new ArcUpdateDto(ARC_NAME, TOTAL_PLANNED_MINUTES);

    @Test
    void shouldReturnArc_whenIdExists() throws Exception {
        when(arcService.findById(ARC.getId())).thenReturn(Optional.of(ARC));

        final var actions = mockMvc.perform(get(ROOT + "/" + ARC.getId().id()))
                .andExpect(status().isOk());

        assertArcJson(actions, ARC);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExists() throws Exception {
        when(arcService.findById(ARC.getId())).thenReturn(Optional.empty());

        mockMvc.perform(get(ROOT + "/" + ARC.getId().id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfArc_whenUserIdExists() throws Exception {
        when(arcService.findAllForUser(USER_ID)).thenReturn(List.of(ARC));

        final var actions = mockMvc.perform(get(ROOT + "/users/" + USER_ID.id()))
                .andExpect(status().isOk());

        assertArcListJson(actions, ARC);
    }

    @Test
    void shouldReturnNotFound_whenUserIdDoesNotExists() throws Exception {
        when(arcService.findAllForUser(USER_ID)).thenReturn(List.of());

        mockMvc.perform(get(ROOT + "/users/" + USER_ID.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUser_whenDataIsValid() throws Exception {
        when(arcService.create(any())).thenReturn(ARC);

        final String json = objectMapper.writeValueAsString(CREATION_DTO);

        final var actions = mockMvc.perform(post(ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        assertArcJson(actions, ARC);
    }

    @Test
    void shouldReturnArc_whenUpdatingExistingArc() throws Exception {
        when(arcService.update(eq(ARC_ID), any())).thenReturn(ARC);

        final String json = objectMapper.writeValueAsString(UPDATE_DTO);

        final var actions = mockMvc.perform(put(ROOT + "/" + ARC_ID.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        assertArcJson(actions, ARC);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingArc() throws Exception {
        mockMvc.perform(delete(ROOT + "/" + ARC_ID.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllArcForExistingUser() throws Exception {
        mockMvc.perform(delete(ROOT + "/users/" + USER_ID.id()))
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