package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.arc.ArcService;
import com.valentin_d.focusarc.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Arc> getById(@AuthenticationPrincipal final User user,
                                       @PathVariable final ArcId arcId) {
        final var arc = service.findByIdAndOwnerId(arcId, user.getId());
        return ResponseUtil.wrapOrNotFound(arc);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Arc>> getAllForCurrentUser(@AuthenticationPrincipal final User user) {
        final var userArcs = service.findAllForUser(user.getId());
        return ResponseUtil.wrapOrNoContent(userArcs);
    }

    @PostMapping
    public ResponseEntity<Arc> create(@AuthenticationPrincipal final User user,
                                      @Valid @RequestBody final ArcCreationDto arcCreationDto) {
        final var arc = service.create(user.getId(), arcCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(arc);
    }

    @PutMapping("/{arcId}")
    public ResponseEntity<Arc> update(@AuthenticationPrincipal final User user,
                                      @PathVariable final ArcId arcId,
                                      @Valid @RequestBody final ArcUpdateDto arcUpdateDto) {
        final var arc = service.update(user.getId(), arcId, arcUpdateDto);
        return ResponseEntity.ok(arc);
    }

    @DeleteMapping("/{arcId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user,
                                       @PathVariable final ArcId arcId) {
        service.delete(user.getId(), arcId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAllForCurrentUser(@AuthenticationPrincipal final User user) {
        service.deleteAllForUser(user.getId());
        return ResponseEntity.noContent().build();
    }
}