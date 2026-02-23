package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.service.TaskService;
import com.valentin_d.focusarc.util.ResponseUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService service;

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getById(@PathVariable TaskId taskId) {
        final var arc = service.findById(taskId);
        return ResponseUtil.wrapOrNotFound(arc);
    }

    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<List<Task>> getAllForChapter(@PathVariable ChapterId chapterId) {
        final var chapterTasks = service.findAllForChapter(chapterId);
        return ResponseUtil.wrapOrNotFound(chapterTasks);
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody final TaskCreationDto taskCreationDto) {
        final var task = service.create(taskCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> update(@PathVariable @NotNull final TaskId taskId,
                                       @Valid @RequestBody final TaskUpdateDto taskUpdateDto) {
        final var arc = service.update(taskId, taskUpdateDto);
        return ResponseEntity.ok(arc);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull final TaskId taskId) {
        service.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteAllForUser(@PathVariable ChapterId chapterId) {
        service.deleteAllForChapter(chapterId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable @NotNull final TaskId taskId,
                                             @Valid @RequestBody final TaskCompleteDto taskCompleteDto) {
        service.completeTask(taskId, taskCompleteDto);
        return ResponseEntity.ok().build();
    }
}