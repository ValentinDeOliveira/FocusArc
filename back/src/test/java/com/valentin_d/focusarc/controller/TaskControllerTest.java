package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.TaskAssertion;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.task.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest extends BaseControllerTest {
    @MockitoBean
    private TaskService taskService;
    private final static String ROOT = "/tasks";
    private final TaskAssertion taskAssertion = new TaskAssertion();

    @Test
    void shouldReturnChapter_whenIdExists() throws Exception {
        final var task = aTask();
        when(taskService.findById(task.getId())).thenReturn(Optional.of(task));

        final var actions = mvcGet(ROOT + "/" + task.getId().id())
                .andExpect(status().isOk());

        taskAssertion.assertSingleJson(actions, task);
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

        taskAssertion.assertListJson(actions, task);
    }

    @Test
    void shouldReturnNoContent_whenChapterHasNoTasks() throws Exception {
        when(taskService.findAllForChapter(any())).thenReturn(List.of());

        mvcGet(ROOT + "/chapters/" + ChapterId.random().id())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateChapter_whenDataIsValid() throws Exception {
        final var task = aTask();
        final var creationDto = aTaskCreationDto();

        when(taskService.create(any())).thenReturn(task);

        final var json = toJson(creationDto);

        final var actions = mvcPost(ROOT, json)
                .andExpect(status().isCreated());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnChapter_whenUpdatingExistingChapter() throws Exception {
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskService.update(eq(task.getId()), any())).thenReturn(task);

        final String json = toJson(updateDto);

        final var actions = mvcPut(ROOT + "/" + task.getId().id(), json)
                .andExpect(status().isOk());

        taskAssertion.assertSingleJson(actions, task);
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

    @Test
    void shouldReturnTasks_whenGettingTodayTask() throws Exception {
        final var task = aTask();

        when(taskService.getTodaysTasks(any())).thenReturn(List.of(task));

        final var actions = mvcGet(ROOT + "/today?userId=" + UserId.random().id())
                .andExpect(status().isOk());

        taskAssertion.assertListJson(actions, task);
    }

    @Test
    void shouldReturnNoContent_whenGettingTodayTaskWhenNoTask() throws Exception {
        when(taskService.getTodaysTasks(any())).thenReturn(List.of());

        mvcGet(ROOT + "/today?userId=" + UserId.random().id())
                .andExpect(status().isNoContent());
    }
}