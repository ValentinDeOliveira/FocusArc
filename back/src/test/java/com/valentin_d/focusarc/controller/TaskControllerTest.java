package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.TaskAssertion;
import com.valentin_d.focusarc.exception.task.TaskAlreadyFinishedException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.service.task.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
        final var task = aTask();
        when(taskService.findById(task.getId(), user.getId())).thenReturn(Optional.of(task));

        final var actions = mvcGetWithUser(taskUrl(task.getId()), user)
                .andExpect(status().isOk());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnNotFoundOnGetById_whenIdDoesNotExists() throws Exception {
        final var task = aTask();
        when(taskService.findById(task.getId(), user.getId())).thenReturn(Optional.empty());

        mvcGetWithUser(taskUrl(task.getId()), user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfTasks_whenChapterIdExists() throws Exception {
        final var task = aTask();
        when(taskService.findAllForChapter(task.getChapter(), user.getId())).thenReturn(List.of(task));

        final var actions = mvcGetWithUser(chaptersUrl(task.getChapter()), user)
                .andExpect(status().isOk());

        taskAssertion.assertListJson(actions, task);
    }

    @Test
    void shouldCreateTask_whenDataIsValid() throws Exception {
        final var task = aTask();
        final var creationDto = aTaskCreationDto();

        when(taskService.create(creationDto, user.getId())).thenReturn(task);

        final var actions = mvcPostWithUser(ROOT, toJson(creationDto), user)
                .andExpect(status().isCreated());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnTask_whenUpdatingExistingTask() throws Exception {
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskService.update(task.getId(), updateDto, user.getId())).thenReturn(task);

        final var actions = mvcPutWithUser(taskUrl(task.getId()), toJson(updateDto), user)
                .andExpect(status().isOk());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingTask() throws Exception {
        final var task = aTask();

        mvcDeleteWithUser(taskUrl(task.getId()), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllTasksForExistingChapter() throws Exception {
        final var task = aTask();

        mvcDeleteWithUser(chaptersUrl(task.getChapter()), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnOk_whenStartingPlannedTask() throws Exception {
        final var task = aTaskWithStatus(TaskStatus.PLANNED);

        when(taskService.startTask(task.getId(), user.getId())).thenReturn(task);

        final var actions = mvcPatchWithUser(taskUrl(task.getId()) + "/start", "", user)
                .andExpect(status().isOk());

        taskAssertion.assertSingleJson(actions, task);
    }

    @Test
    void shouldReturnBadRequest_whenStartingFinishedTask() throws Exception {
        final var task = aTaskWithStatus(TaskStatus.DONE);

        doThrow(new TaskAlreadyFinishedException(task.getId(), task.getStatus()))
                .when(taskService).startTask(task.getId(), user.getId());

        mvcPatchWithUser(taskUrl(task.getId()) + "/start", "", user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOk_whenCompletingExistingTask() throws Exception {
        final var task = aTask();
        final var completeDto = aTaskCompleteDto();

        mvcPatchWithUser(taskUrl(task.getId()) + "/complete", toJson(completeDto), user)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequest_whenCompletingFinishedTask() throws Exception {
        final var task = aTaskWithStatus(TaskStatus.DONE);
        final var completeDto = aTaskCompleteDto();

        doThrow(new TaskAlreadyFinishedException(task.getId(), task.getStatus()))
                .when(taskService).completeTask(task.getId(), user.getId(), completeDto);

        mvcPatchWithUser(taskUrl(task.getId()) + "/complete", toJson(completeDto), user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTasks_whenGettingTodayTask() throws Exception {
        final var task = aTask();

        when(taskService.getTodaysTasks(any(UserId.class))).thenReturn(List.of(task));

        final var actions = mvcGetWithUser(ROOT + "/today", user)
                .andExpect(status().isOk());

        taskAssertion.assertListJson(actions, task);
    }

    private String taskUrl(TaskId taskId) {
        return ROOT + "/" + taskId.id();
    }

    private String chaptersUrl(ChapterId chapterId) {
        return ROOT + "/chapters/" + chapterId.id();
    }
}