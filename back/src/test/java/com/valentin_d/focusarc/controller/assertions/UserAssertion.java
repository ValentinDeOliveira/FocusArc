package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.model.User;
import org.springframework.test.web.servlet.ResultActions;

public class UserAssertion extends BaseAssertion<User> {

    @Override
    protected void assertJson(final ResultActions actions, final String path, final User expected) throws Exception {
        expect(actions, path, "id", expected.getId().id().toString());
        expect(actions, path, "name", expected.getName());
        expect(actions, path, "email", expected.getEmail());
        expect(actions, path, "lastLogin", formatDateTime(expected.getLastLogin()));
    }
}