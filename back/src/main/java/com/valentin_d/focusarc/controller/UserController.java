package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.user.UserService;
import com.valentin_d.focusarc.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<User> getById(@AuthenticationPrincipal final User user) {
        final var foundUser = service.findById(user.getId());
        return ResponseUtil.wrapOrNotFound(foundUser);
    }

    @PutMapping
    public ResponseEntity<User> update(@AuthenticationPrincipal final User user,
                                       @Valid @RequestBody final UserUpdateDto userUpdateDto) {
        final var updated = service.update(user.getId(), userUpdateDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user) {
        service.delete(user.getId());
        return ResponseEntity.noContent().build();
    }
}