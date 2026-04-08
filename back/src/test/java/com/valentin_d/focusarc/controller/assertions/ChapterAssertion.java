package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.model.Chapter;
import org.springframework.test.web.servlet.ResultActions;

public class ChapterAssertion extends BaseAssertion<Chapter> {

    @Override
    protected void assertJson(final ResultActions actions, final String path, final Chapter expected) throws Exception {
        expect(actions, path, "id", expected.getId().id().toString());
        expect(actions, path, "arc", expected.getArc().id().toString());
        expect(actions, path, "estimatedMinutes", expected.getEstimatedMinutes());
        expect(actions, path, "completedMinutes", expected.getCompletedMinutes());
        expect(actions, path, "scheduledDate", expected.getScheduledDate().toString());
        expect(actions, path, "allTasksDone", expected.isAllTasksDone());
    }
}