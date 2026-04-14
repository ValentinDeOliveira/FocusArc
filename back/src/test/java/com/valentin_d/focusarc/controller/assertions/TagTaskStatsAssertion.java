package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.dto.tag.TagTaskStatsDto;
import org.springframework.test.web.servlet.ResultActions;

public class TagTaskStatsAssertion extends BaseAssertion<TagTaskStatsDto> {
    @Override
    protected void assertJson(final ResultActions actions, final String path,
                              final TagTaskStatsDto expected) throws Exception {
        expect(actions, path, "tagId", expected.tagId().id().toString());
        expect(actions, path, "total", expected.total());
        expect(actions, path, "done", expected.done());
    }
}