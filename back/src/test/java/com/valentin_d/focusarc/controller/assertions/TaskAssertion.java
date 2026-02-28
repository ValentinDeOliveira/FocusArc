package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.model.task.Task;
import org.springframework.test.web.servlet.ResultActions;

public class TaskAssertion extends BaseAssertion<Task> {

    @Override
    protected void assertJson(final ResultActions actions, final String path, final Task expected) throws Exception {
        expect(actions, path, "id", expected.getId().id().toString());
        expect(actions, path, "chapter", expected.getChapter().id().toString());
        expect(actions, path, "estimatedMinutes", expected.getEstimatedMinutes());
        expect(actions, path, "completedMinutes", expected.getCompletedMinutes());
        expect(actions, path, "scheduledAt", expected.getScheduledAt().toString());
        expect(actions, path, "status", expected.getStatus().name());
    }
}