package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.arc.ArcService;
import com.valentin_d.focusarc.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arcs")
@RequiredArgsConstructor
@Validated
public class ArcController {

    private final ArcService service;

    @GetMapping("/{arcId}")
    public ResponseEntity<Arc> getById(@PathVariable final ArcId arcId) {
        final var arc = service.findById(arcId);
        return ResponseUtil.wrapOrNotFound(arc);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Arc>> getAllForUser(@PathVariable final UserId userId) {
        final var userArcs = service.findAllForUser(userId);
        return ResponseUtil.wrapOrNoContent(userArcs);
    }

    @PostMapping
    public ResponseEntity<Arc> create(@Valid @RequestBody final ArcCreationDto arcCreationDto) {
        final var arc = service.create(arcCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(arc);
    }

    @PutMapping("/{arcId}")
    public ResponseEntity<Arc> update(@PathVariable final ArcId arcId,
                                       @Valid @RequestBody final ArcUpdateDto arcUpdateDto) {
        final var arc = service.update(arcId, arcUpdateDto);
        return ResponseEntity.ok(arc);
    }

    @DeleteMapping("/{arcId}")
    public ResponseEntity<Void> delete(@PathVariable final ArcId arcId) {
        service.delete(arcId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteAllForUser(@PathVariable final UserId userId) {
        service.deleteAllForUser(userId);
        return ResponseEntity.noContent().build();
    }
}
