package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.model.arc.Arc;
import org.springframework.test.web.servlet.ResultActions;

public class ArcAssertion extends BaseAssertion<Arc> {

    @Override
    protected void assertJson(final ResultActions actions, final String path, final Arc expected) throws Exception {
        expect(actions, path, "id", expected.getId().id().toString());
        expect(actions, path, "owner", expected.getOwner().id().toString());
        expect(actions, path, "name", expected.getName());
        expect(actions, path, "totalEstimatedMinutes", expected.getTotalEstimatedMinutes());
        expect(actions, path, "totalCompletedMinutes", expected.getTotalCompletedMinutes());
        expect(actions, path, "status", expected.getStatus().name());
        expect(actions, path, "startDate", expected.getStartDate().toString());
        expect(actions, path, "endDate", expected.getEndDate().toString());
    }
}