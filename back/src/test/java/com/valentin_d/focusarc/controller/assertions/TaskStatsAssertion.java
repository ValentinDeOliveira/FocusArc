package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.dto.task.TaskStatsDto;
import org.springframework.test.web.servlet.ResultActions;

public class TaskStatsAssertion extends BaseAssertion<TaskStatsDto> {
    @Override
    protected void assertJson(final ResultActions actions, final String path,
                              final TaskStatsDto expected) throws Exception {
        expect(actions, path, "taskStatus", expected.taskStatus().name());
        expect(actions, path, "total", expected.total());
        expect(actions, path, "done", expected.done());
    }
}