package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.seed.SeedResponseDto;
import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.auth.CookieService;
import com.valentin_d.focusarc.service.auth.JwtService;
import com.valentin_d.focusarc.service.seed.SeedService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/dev")
@Profile("!test")
@RequiredArgsConstructor
public class SeedController {
    private final SeedService seedService;
    private final JwtService jwtService;
    private final CookieService cookieService;

    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    public void seedScheduled() {
        seedService.doSeed();
    }

    @PostMapping("/seed")
    public ResponseEntity<SeedResponseDto> seed(@AuthenticationPrincipal User caller,
                                                final HttpServletResponse response) {
        // if endpoint get call, ensure seeded user only can re-seed
        if (!caller.getEmail().equals(SeedService.SEED_EMAIL)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        final var user = seedService.doSeed();
        cookieService.setAuthCookies(response, new AuthResponseDto(jwtService.generateToken(user),
                jwtService.generateRefreshToken(user)));

        return ResponseEntity.ok(new SeedResponseDto(SeedService.SEED_EMAIL, SeedService.SEED_PASSWORD));
    }
}