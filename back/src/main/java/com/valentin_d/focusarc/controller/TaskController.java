package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.service.task.TaskService;
import com.valentin_d.focusarc.util.ResponseUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Task> getById(@PathVariable TaskId taskId) {
        final var task = service.findById(taskId);
        return ResponseUtil.wrapOrNotFound(task);
    }

    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<List<Task>> getAllForChapter(@PathVariable ChapterId chapterId) {
        final var chapterTasks = service.findAllForChapter(chapterId);
        return ResponseUtil.wrapOrNoContent(chapterTasks);
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody final TaskCreationDto taskCreationDto) {
        final var task = service.create(taskCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> update(@PathVariable final TaskId taskId,
                                       @Valid @RequestBody final TaskUpdateDto taskUpdateDto) {
        final var task = service.update(taskId, taskUpdateDto);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable final TaskId taskId) {
        service.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteAllForChapter(@PathVariable ChapterId chapterId) {
        service.deleteAllForChapter(chapterId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable final TaskId taskId,
                                             @Valid @RequestBody final TaskCompleteDto taskCompleteDto) {
        service.completeTask(taskId, taskCompleteDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTodayTask(@RequestParam("userId") @NotNull final UserId userId) {
        return wrapOrNoContent(service.getTodaysTasks(userId));
    }
}