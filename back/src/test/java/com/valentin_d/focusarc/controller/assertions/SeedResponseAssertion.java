package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.dto.seed.SeedResponseDto;
import org.springframework.test.web.servlet.ResultActions;

public class SeedResponseAssertion extends BaseAssertion<SeedResponseDto> {

    @Override
    protected void assertJson(ResultActions actions, String path, SeedResponseDto expected) throws Exception {
        expect(actions, path, "email", expected.email());
        expect(actions, path, "password", expected.password());

    }
}