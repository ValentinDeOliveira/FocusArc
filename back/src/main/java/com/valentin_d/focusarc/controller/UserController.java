package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.user.UserCreationDto;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.service.UserService;
import com.valentin_d.focusarc.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserCreationDto userCreationDto) {
        final var user = service.create(userCreationDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public ResponseEntity<User> getByEmail(@RequestParam("email") String email) {
        final var user = service.getByEmail(email);
        return ResponseUtil.wrapOrNotFound(user);
    }
}
