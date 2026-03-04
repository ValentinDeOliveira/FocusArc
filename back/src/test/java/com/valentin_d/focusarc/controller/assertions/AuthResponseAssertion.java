package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.model.auth.AuthResponseDto;
import org.springframework.test.web.servlet.ResultActions;

public class AuthResponseAssertion extends BaseAssertion<AuthResponseDto> {

    @Override
    protected void assertJson(final ResultActions actions, final String path, final AuthResponseDto expected) throws Exception {
        expect(actions, path, "accessToken", expected.accessToken());
        expect(actions, path, "refreshToken", expected.refreshToken());
    }
}