package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.dto.user.UserUpdateDto;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.user.UserService;
import com.valentin_d.focusarc.util.ResponseUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody final UserCreationDto userCreationDto) {
        final var user = service.create(userCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/email")
    public ResponseEntity<User> getByEmail(@RequestParam("email") @NotBlank @Email final String email) {
        final var user = service.findByEmail(email);
        return ResponseUtil.wrapOrNotFound(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable @NotNull final UserId id) {
        final var user = service.findById(id);
        return ResponseUtil.wrapOrNotFound(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable @NotNull final UserId id,
                                       @Valid @RequestBody final UserUpdateDto userUpdateDto) {
        final var user = service.update(userUpdateDto, id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull final UserId id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}