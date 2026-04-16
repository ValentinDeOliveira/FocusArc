package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.dto.tag.TagTaskStatsDto;
import com.valentin_d.focusarc.dto.task.TaskStatsDto;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.arc.ArcService;
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

@Tag(name = "Arcs", description = "A multi-week focus span — at most one ACTIVE arc per user")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/arcs")
@RequiredArgsConstructor
@Validated
public class ArcController {
    private final ArcService service;

    @Operation(summary = "Get an arc by ID")
    @GetMapping("/{arcId}")
    public ResponseEntity<Arc> getById(@AuthenticationPrincipal final User user,
                                       @PathVariable final ArcId arcId) {
        final var arc = service.findByIdAndOwnerId(arcId, user.getId());
        return ResponseUtil.wrapOrNotFound(arc);
    }

    @Operation(summary = "Get all arcs for the authenticated user")
    @ApiResponse(responseCode = "204", description = "No arcs found")
    @GetMapping("/me")
    public ResponseEntity<List<Arc>> getAllForCurrentUser(@AuthenticationPrincipal final User user) {
        final var userArcs = service.findAllForUser(user.getId());
        return ResponseUtil.wrapOrNoContent(userArcs);
    }

    @Operation(summary = "Create a new arc")
    @ApiResponse(responseCode = "400", description = "User already has an active arc")
    @PostMapping
    public ResponseEntity<Arc> create(@AuthenticationPrincipal final User user,
                                      @Valid @RequestBody final ArcCreationDto arcCreationDto) {
        final var arc = service.create(user.getId(), arcCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(arc);
    }

    @Operation(summary = "Update an arc", description = "Partial update, null fields are ignored.")
    @PutMapping("/{arcId}")
    public ResponseEntity<Arc> update(@AuthenticationPrincipal final User user,
                                      @PathVariable final ArcId arcId,
                                      @Valid @RequestBody final ArcUpdateDto arcUpdateDto) {
        final var arc = service.update(user.getId(), arcId, arcUpdateDto);
        return ResponseEntity.ok(arc);
    }

    @Operation(summary = "Delete an arc by ID")
    @DeleteMapping("/{arcId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user,
                                       @PathVariable final ArcId arcId) {
        service.delete(user.getId(), arcId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all arcs for the authenticated user")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllForCurrentUser(@AuthenticationPrincipal final User user) {
        service.deleteAllForUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<ArcSummaryResponseDto> getArcSummary(@AuthenticationPrincipal final User user) {
        final var summary = service.getSummaryForUser(user.getId());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/tag-stats")
    public ResponseEntity<List<TagTaskStatsDto>> getTagTasksStats(@AuthenticationPrincipal final User user) {
        final var stats = service.getTagTaskStats(user.getId());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/task-stats")
    public ResponseEntity<List<TaskStatsDto>> getTasksStats(@AuthenticationPrincipal final User user) {
        final var stats = service.getTaskStats(user.getId());
        return ResponseEntity.ok(stats);
    }
}