package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.model.tag.Tag;
import org.springframework.test.web.servlet.ResultActions;

public class TagAssertion extends BaseAssertion<Tag> {

    @Override
    protected void assertJson(final ResultActions actions, final String path, final Tag expected) throws Exception {
        expect(actions, path, "id", expected.getId().id().toString());
        expect(actions, path, "owner", expected.getOwner().id().toString());
        expect(actions, path, "label", expected.getLabel());
        expect(actions, path, "color", expected.getColor().name());
    }
}
