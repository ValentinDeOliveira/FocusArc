package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.SeedResponseAssertion;
import com.valentin_d.focusarc.dto.seed.SeedResponseDto;
import com.valentin_d.focusarc.service.auth.CookieService;
import com.valentin_d.focusarc.service.seed.SeedService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUserWithEmailAndPassword;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeedController.class)
class SeedControllerTest extends BaseSecurityControllerTest {
    @MockitoBean
    private SeedService seedService;
    @MockitoBean
    private CookieService cookieService;

    private final static String ROOT = "/dev";
    private final SeedResponseAssertion seedResponseAssertion = new SeedResponseAssertion();

    @Test
    void shouldReturnSeed_whenCallerSeededUser() throws Exception {
        final var seededUser = aUserWithEmailAndPassword(SeedService.SEED_EMAIL, SeedService.SEED_PASSWORD);

        final var actions = mvcPostWithUser(ROOT + "/seed", seededUser)
                .andExpect(status().isOk());

        seedResponseAssertion.assertSingleJson(actions, new SeedResponseDto(seededUser.getEmail(),
                seededUser.getPassword()));
    }

    @Test
    void shouldThrowErrorOnSeed_whenCallerIsNotSeededUser() throws Exception {
        mvcPostWithUser(ROOT + "/seed", user)
                .andExpect(status().isForbidden());
    }
}