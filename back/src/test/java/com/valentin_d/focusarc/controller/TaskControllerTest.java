package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest extends BaseControllerTest {
    @MockitoBean
    private TaskService taskService;

    private final static String ROOT = "/tasks";

    @Test
    void shouldReturnChapter_whenIdExists() throws Exception {
        final var task = aTask();
        when(taskService.findById(task.getId())).thenReturn(Optional.of(task));

        final var actions = mvcGet(ROOT + "/" + task.getId().id())
                .andExpect(status().isOk());

        assertTaskJson(actions, task);
    }

    @Test
    void shouldReturnNotFoundOnGetById_whenIdDoesNotExists() throws Exception {
        final var task = aTask();
        when(taskService.findById(task.getId())).thenReturn(Optional.empty());

        mvcGet(ROOT + "/" + task.getId().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfChapter_whenArcIdExists() throws Exception {
        final var task = aTask();
        when(taskService.findAllForChapter(task.getChapter())).thenReturn(List.of(task));

        final var actions = mvcGet(ROOT + "/chapters/" + task.getChapter().id())
                .andExpect(status().isOk());

        assertTaskListJson(actions, task);
    }

    @Test
    void shouldReturnNotFound_whenArcIdDoesNotExists() throws Exception {
        when(taskService.findAllForChapter(any())).thenReturn(List.of());

        mvcGet(ROOT + "/chapters/" + ChapterId.random().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateChapter_whenDataIsValid() throws Exception {
        final var task = aTask();
        final var creationDto = aTaskCreationDto();

        when(taskService.create(any())).thenReturn(task);

        final var json = toJson(creationDto);

        final var actions = mvcPost(ROOT, json)
                .andExpect(status().isCreated());

        assertTaskJson(actions, task);
    }

    @Test
    void shouldReturnChapter_whenUpdatingExistingChapter() throws Exception {
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskService.update(eq(task.getId()), any())).thenReturn(task);

        final String json = toJson(updateDto);

        final var actions = mvcPut(ROOT + "/" + task.getId().id(), json)
                .andExpect(status().isOk());

        assertTaskJson(actions, task);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingChapter() throws Exception {
        final var task = aTask();

        mvcDelete(ROOT + "/" + task.getId().id())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllChapterForExistingArc() throws Exception {
        final var task = aTask();

        mvcDelete(ROOT + "/chapters/" + task.getChapter().id())
                .andExpect(status().isNoContent());
    }

    private void assertTaskJson(final ResultActions actions, final Task expected) throws Exception {
        assertTaskJson(actions, "$", expected);
    }

    private void assertTaskListJson(final ResultActions actions, final Task expected) throws Exception {
        assertTaskJson(actions, "$[0]", expected);
    }

    private void assertTaskJson(final ResultActions actions, final String path, final Task expected) throws Exception {
        actions
                .andExpect(jsonPath(path + ".id").value(expected.getId().id().toString()))
                .andExpect(jsonPath(path + ".chapter").value(expected.getChapter().id().toString()))
                .andExpect(jsonPath(path + ".estimatedMinutes").value(expected.getEstimatedMinutes()))
                .andExpect(jsonPath(path + ".completedMinutes").value(expected.getCompletedMinutes()))
                .andExpect(jsonPath(path + ".scheduledAt").value(expected.getScheduledAt().toString()))
                .andExpect(jsonPath(path + ".status").value(expected.getStatus().name()));
    }
}