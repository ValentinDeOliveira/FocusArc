package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.user.UserService;
import com.valentin_d.focusarc.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Operates on the authenticated user — no {id} in path")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @Operation(summary = "Get the authenticated user")
    @GetMapping
    public ResponseEntity<User> getById(@AuthenticationPrincipal final User user) {
        final var foundUser = service.findById(user.getId());
        return ResponseUtil.wrapOrNotFound(foundUser);
    }

    @Operation(
            summary = "Update the authenticated user",
            description = "Only `name` is updatable. Null fields are ignored."
    )
    @PutMapping
    public ResponseEntity<User> update(@AuthenticationPrincipal final User user,
                                       @Valid @RequestBody final UserUpdateDto userUpdateDto) {
        final var updated = service.update(user.getId(), userUpdateDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete the authenticated user")
    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user) {
        service.delete(user.getId());
        return ResponseEntity.noContent().build();
    }
}