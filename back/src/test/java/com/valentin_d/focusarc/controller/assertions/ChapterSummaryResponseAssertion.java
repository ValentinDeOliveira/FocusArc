package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.dto.chapter.ChapterSummaryResponseDto;
import org.springframework.test.web.servlet.ResultActions;

public class ChapterSummaryResponseAssertion extends BaseAssertion<ChapterSummaryResponseDto> {
    private final TaskAssertion taskAssertion = new TaskAssertion();

    @Override
    protected void assertJson(final ResultActions actions, final String path, final ChapterSummaryResponseDto expected) throws Exception {
        expect(actions, path, "chapterId", expected.chapterId().id().toString());
        taskAssertion.assertListPathJson(actions, "tasksToComplete", expected.tasksToComplete());
        expect(actions, path, "estimatedMinutes", expected.estimatedMinutes());
        expect(actions, path, "completedMinutes", expected.completedMinutes());
        expect(actions, path, "remainingTime", expected.remainingTime());
    }
}