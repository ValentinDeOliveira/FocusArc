package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.ArcAssertion;
import com.valentin_d.focusarc.controller.assertions.ArcSummaryResponseAssertion;
import com.valentin_d.focusarc.controller.assertions.TagTaskStatsAssertion;
import com.valentin_d.focusarc.controller.assertions.TaskStatsAssertion;
import com.valentin_d.focusarc.dto.tag.TagTaskStatsDto;
import com.valentin_d.focusarc.dto.task.TaskStatsDto;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.exception.arc.NoActiveArcException;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.service.arc.ArcService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskRecurrenceDto;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArcController.class)
class ArcControllerTest extends BaseSecurityControllerTest {
    @MockitoBean
    private ArcService arcService;
    private final static String ROOT = "/arcs";
    private final ArcAssertion arcAssertion = new ArcAssertion();
    private final ArcSummaryResponseAssertion arcSummaryResponseAssertion = new ArcSummaryResponseAssertion();
    private final TagTaskStatsAssertion tagTaskStatsAssertion = new TagTaskStatsAssertion();
    private final TaskStatsAssertion taskStatsAssertion = new TaskStatsAssertion();

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
    void shouldReturnActiveArc_whenUserIdExists() throws Exception {
        final var arc = anArcWithOwnerIdAndStatus(user.getId(), ArcStatus.ACTIVE);

        when(arcService.findActiveArcForUser(user.getId())).thenReturn(Optional.of(arc));

        final var actions = mvcGetWithUser(ROOT + "/me/active", user)
                .andExpect(status().isOk());

        arcAssertion.assertSingleJson(actions, arc);
    }

    @Test
    void shouldReturnNoContent_whenUserHasNoActiveArcs() throws Exception {
        when(arcService.findActiveArcForUser(any())).thenReturn(Optional.empty());

        mvcGetWithUser(ROOT + "/me/active", aUser())
                .andExpect(status().isNotFound());
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

    @Test
    void shouldReturnSummary_whenActiveArcExists() throws Exception {
        final var summary = anArcSummaryResponseDtoWithTasks();
        when(arcService.getSummaryForUser(user.getId())).thenReturn(summary);

        final var actions = mvcGetWithUser(ROOT + "/summary", user)
                .andExpect(status().isOk());

        arcSummaryResponseAssertion.assertSingleJson(actions, summary);
    }

    @Test
    void shouldReturnBadRequestOnSummary_whenNoActiveArc() throws Exception {
        doThrow(new NoActiveArcException(user.getId())).when(arcService).getSummaryForUser(user.getId());

        mvcGetWithUser(ROOT + "/summary", user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTagStats_whenActiveArcExists() throws Exception {
        final var tagId = TagId.random();
        final var stats = List.of(new TagTaskStatsDto(tagId, 3L, 2L));

        when(arcService.getTagTaskStats(user.getId())).thenReturn(stats);

        final var actions = mvcGetWithUser(ROOT + "/tag-stats", user)
                .andExpect(status().isOk());

        tagTaskStatsAssertion.assertListJson(actions, stats.get(0));
    }

    @Test
    void shouldReturnBadRequestOnTagStats_whenNoActiveArc() throws Exception {
        doThrow(new NoActiveArcException(user.getId())).when(arcService).getTagTaskStats(user.getId());

        mvcGetWithUser(ROOT + "/tag-stats", user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTaskStats_whenActiveArcExists() throws Exception {
        final var stats = List.of(new TaskStatsDto(TaskStatus.DONE, 3L, 2L));

        when(arcService.getTaskStats(user.getId())).thenReturn(stats);

        final var actions = mvcGetWithUser(ROOT + "/task-stats", user)
                .andExpect(status().isOk());

        taskStatsAssertion.assertListJson(actions, stats.get(0));
    }

    @Test
    void shouldReturnBadRequestOnTaskStats_whenNoActiveArc() throws Exception {
        doThrow(new NoActiveArcException(user.getId())).when(arcService).getTaskStats(user.getId());

        mvcGetWithUser(ROOT + "/task-stats", user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNoContent_whenMassCreateIsSuccessful() throws Exception {
        final var arc = anArcWithOwnerId(user.getId());
        final var body = toJson(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())));

        mvcPostWithUser(massCreateUrl(arc.getId()), body, user)
                .andExpect(status().isNoContent());

        verify(arcService).massCreate(any(), eq(arc.getId()), eq(user.getId()));
    }

    @Test
    void shouldReturnNotFound_whenArcDoesNotExist_onMassCreate() throws Exception {
        final var arcId = ArcId.random();
        final var body = toJson(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())));

        doThrow(new ArcDoesNotExistForUserException(arcId, user.getId()))
                .when(arcService).massCreate(any(), eq(arcId), eq(user.getId()));

        mvcPostWithUser(massCreateUrl(arcId), body, user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnLatestArc_whenArcAndUserExists() throws Exception {
        final var arc = anArcWithOwnerId(user.getId());
        when(arcService.getLatestArc(user.getId())).thenReturn(arc);

        mvcGetWithUser(ROOT + "/latest", user)
                .andExpect(status().isOk());
    }


    private String arcUrl(ArcId arcId) {
        return ROOT + "/" + arcId.id();
    }

    private String massCreateUrl(ArcId arcId) {
        return ROOT + "/" + arcId.id() + "/tasks/init";
    }
}