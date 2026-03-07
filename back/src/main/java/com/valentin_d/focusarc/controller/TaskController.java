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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.valentin_d.focusarc.util.ResponseUtil.wrapOrNoContent;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService service;

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getById(@AuthenticationPrincipal final User user,
                                        @PathVariable TaskId taskId) {
        final var task = service.findById(taskId, user.getId());
        return ResponseUtil.wrapOrNotFound(task);
    }

    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<List<Task>> getAllForChapter(@AuthenticationPrincipal final User user,
                                                       @PathVariable ChapterId chapterId) {
        final var chapterTasks = service.findAllForChapter(chapterId, user.getId());
        return ResponseUtil.wrapOrNoContent(chapterTasks);
    }

    @PostMapping
    public ResponseEntity<Task> create(@AuthenticationPrincipal final User user,
                                       @Valid @RequestBody final TaskCreationDto taskCreationDto) {
        final var task = service.create(taskCreationDto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> update(@AuthenticationPrincipal final User user,
                                       @PathVariable final TaskId taskId,
                                       @Valid @RequestBody final TaskUpdateDto taskUpdateDto) {
        final var task = service.update(taskId, taskUpdateDto, user.getId());
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user,
                                       @PathVariable final TaskId taskId) {
        service.delete(taskId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteAllForChapter(@AuthenticationPrincipal final User user,
                                                    @PathVariable ChapterId chapterId) {
        service.deleteAllForChapter(chapterId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@AuthenticationPrincipal final User user,
                                             @PathVariable final TaskId taskId,
                                             @Valid @RequestBody final TaskCompleteDto taskCompleteDto) {
        service.completeTask(taskId, user.getId(), taskCompleteDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTodayTask(@AuthenticationPrincipal final User user) {
        final var task = service.getTodaysTasks(user.getId());
        return wrapOrNoContent(task);
    }
}