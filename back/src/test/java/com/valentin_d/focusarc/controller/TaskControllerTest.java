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
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest extends BaseSecurityControllerTest {
    @MockitoBean
    private TaskService taskService;
    private final static String ROOT = "/tasks";
    private final TaskAssertion taskAssertion = new TaskAssertion();

    @Test
    void shouldReturnTask_whenIdExists() throws Exception {
        final var user = aUser();
        final var task = aTask();
        when(taskService.findById(task.getId(), user.getId())).thenReturn(Optional.of(task));

        final var actions = mvcGetWithUser(ROOT + "/" + task.getId().id(), user)
                .andExpect(status().isOk());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnNotFoundOnGetById_whenIdDoesNotExists() throws Exception {
        final var user = aUser();
        final var task = aTask();
        when(taskService.findById(task.getId(), user.getId())).thenReturn(Optional.empty());

        mvcGetWithUser(ROOT + "/" + task.getId().id(), user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfTasks_whenChapterIdExists() throws Exception {
        final var user = aUser();
        final var task = aTask();
        when(taskService.findAllForChapter(task.getChapter(), user.getId())).thenReturn(List.of(task));

        final var actions = mvcGetWithUser(ROOT + "/chapters/" + task.getChapter().id(), user)
                .andExpect(status().isOk());

        taskAssertion.assertListJson(actions, task);
    }

    @Test
    void shouldReturnNoContent_whenChapterHasNoTasks() throws Exception {
        final var user = aUser();
        when(taskService.findAllForChapter(any(ChapterId.class), any(UserId.class))).thenReturn(List.of());

        mvcGetWithUser(ROOT + "/chapters/" + ChapterId.random().id(), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateTask_whenDataIsValid() throws Exception {
        final var user = aUser();
        final var task = aTask();
        final var creationDto = aTaskCreationDto();

        when(taskService.create(creationDto, user.getId())).thenReturn(task);

        final var actions = mvcPostWithUser(ROOT, toJson(creationDto), user)
                .andExpect(status().isCreated());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnTask_whenUpdatingExistingTask() throws Exception {
        final var user = aUser();
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskService.update(task.getId(), updateDto, user.getId())).thenReturn(task);

        final var actions = mvcPutWithUser(ROOT + "/" + task.getId().id(), toJson(updateDto), user)
                .andExpect(status().isOk());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingTask() throws Exception {
        final var user = aUser();
        final var task = aTask();

        mvcDeleteWithUser(ROOT + "/" + task.getId().id(), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllTasksForExistingChapter() throws Exception {
        final var user = aUser();
        final var task = aTask();

        mvcDeleteWithUser(ROOT + "/chapters/" + task.getChapter().id(), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnOk_whenCompletingExistingTask() throws Exception {
        final var user = aUser();
        final var task = aTask();
        final var completeDto = aTaskCompleteDto();

        mvcPatchWithUser(ROOT + "/" + task.getId().id() + "/complete", toJson(completeDto), user)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnTasks_whenGettingTodayTask() throws Exception {
        final var user = aUser();
        final var task = aTask();

        when(taskService.getTodaysTasks(any(UserId.class))).thenReturn(List.of(task));

        final var actions = mvcGetWithUser(ROOT + "/today", user)
                .andExpect(status().isOk());

        taskAssertion.assertListJson(actions, task);
    }

    @Test
    void shouldReturnNoContent_whenGettingTodayTaskWhenNoTask() throws Exception {
        final var user = aUser();
        when(taskService.getTodaysTasks(any(UserId.class))).thenReturn(List.of());

        mvcGetWithUser(ROOT + "/today", user)
                .andExpect(status().isNoContent());
    }
}