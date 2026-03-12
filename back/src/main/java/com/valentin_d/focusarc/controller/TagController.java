package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.tag.TagCreationDto;
import com.valentin_d.focusarc.dto.tag.TagUpdateDto;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.tag.Tag;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.tag.TagService;
import com.valentin_d.focusarc.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "User-defined labels for tasks")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Validated
public class TagController {
    private final TagService service;

    @Operation(summary = "Get a tag by ID")
    @GetMapping("/{tagId}")
    public ResponseEntity<Tag> getById(@AuthenticationPrincipal final User user,
                                       @PathVariable final TagId tagId) {
        return ResponseUtil.wrapOrNotFound(service.findByIdAndOwnerId(tagId, user.getId()));
    }

    @Operation(summary = "Get all tags for the authenticated user")
    @ApiResponse(responseCode = "204", description = "No tags found")
    @GetMapping("/me")
    public ResponseEntity<List<Tag>> getAllForCurrentUser(@AuthenticationPrincipal final User user) {
        return ResponseUtil.wrapOrNoContent(service.findAllForUser(user.getId()));
    }

    @Operation(summary = "Create a new tag")
    @PostMapping
    public ResponseEntity<Tag> create(@AuthenticationPrincipal final User user,
                                      @Valid @RequestBody final TagCreationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(user.getId(), dto));
    }

    @Operation(summary = "Update a tag", description = "Partial update, null fields are ignored.")
    @PutMapping("/{tagId}")
    public ResponseEntity<Tag> update(@AuthenticationPrincipal final User user,
                                      @PathVariable final TagId tagId,
                                      @Valid @RequestBody final TagUpdateDto dto) {
        return ResponseEntity.ok(service.update(user.getId(), tagId, dto));
    }

    @Operation(summary = "Delete a tag by ID")
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user,
                                       @PathVariable final TagId tagId) {
        service.delete(user.getId(), tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all tags for the authenticated user")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllForCurrentUser(@AuthenticationPrincipal final User user) {
        service.deleteAllForUser(user.getId());
        return ResponseEntity.noContent().build();
    }
}
