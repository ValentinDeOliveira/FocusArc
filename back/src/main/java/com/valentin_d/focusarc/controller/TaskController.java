package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.task.TaskService;
import com.valentin_d.focusarc.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.valentin_d.focusarc.util.ResponseUtil.wrapOrNoContent;

@Tag(name = "Tasks", description = "Work blocks within a chapter")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {
    private final TaskService service;

    @Operation(summary = "Get a task by ID")
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getById(@AuthenticationPrincipal final User user,
                                        @PathVariable TaskId taskId) {
        final var task = service.findById(taskId, user.getId());
        return ResponseUtil.wrapOrNotFound(task);
    }

    @Operation(summary = "Get all tasks for a chapter")
    @ApiResponse(responseCode = "204", description = "No tasks found")
    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<List<Task>> getAllForChapter(@AuthenticationPrincipal final User user,
                                                       @PathVariable ChapterId chapterId) {
        final var chapterTasks = service.findAllForChapter(chapterId, user.getId());
        return ResponseUtil.wrapOrNoContent(chapterTasks);
    }

    @Operation(summary = "Create a task")
    @PostMapping
    public ResponseEntity<Task> create(@AuthenticationPrincipal final User user,
                                       @Valid @RequestBody final TaskCreationDto taskCreationDto) {
        final var task = service.create(taskCreationDto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @Operation(
            summary = "Update a task",
            description = "Partial update, null fields are ignored. " +
                    "Triggers chapter and arc recalculation if minutes change."
    )
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> update(@AuthenticationPrincipal final User user,
                                       @PathVariable final TaskId taskId,
                                       @Valid @RequestBody final TaskUpdateDto taskUpdateDto) {
        final var task = service.update(taskId, taskUpdateDto, user.getId());
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Delete a task by ID")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user,
                                       @PathVariable final TaskId taskId) {
        service.delete(taskId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all tasks for a chapter")
    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteAllForChapter(@AuthenticationPrincipal final User user,
                                                    @PathVariable ChapterId chapterId) {
        service.deleteAllForChapter(chapterId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Start a task",
            description = "Sets status to `IN_PROGRESS` and records the start timestamp. " +
                    "Only pending tasks (PLANNED or IN_PROGRESS) can be started."
    )
    @ApiResponse(responseCode = "400", description = "Task is already finished (DONE or SKIPPED)")
    @PatchMapping("/{taskId}/start")
    public ResponseEntity<Task> startTask(@AuthenticationPrincipal final User user,
                                          @PathVariable final TaskId taskId) {
        final var task = service.startTask(taskId, user.getId());
        return ResponseEntity.ok(task);
    }

    @Operation(
            summary = "Complete a task",
            description = "Sets status to `DONE` and records `completedMinutes`. " +
                    "Triggers chapter and arc recalculation."
    )
    @ApiResponse(responseCode = "400", description = "Task is already done")
    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Task> completeTask(@AuthenticationPrincipal final User user,
                                             @PathVariable final TaskId taskId,
                                             @Valid @RequestBody final TaskCompleteDto taskCompleteDto) {
        final var task = service.completeTask(taskId, user.getId(), taskCompleteDto);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Get today's tasks",
            description = "Tasks for today's chapter in the user's active arc. User resolved from JWT.")
    @ApiResponse(responseCode = "204", description = "No tasks today")
    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTodayTask(@AuthenticationPrincipal final User user) {
        final var task = service.getTodaysTasks(user.getId());
        return wrapOrNoContent(task);
    }
}