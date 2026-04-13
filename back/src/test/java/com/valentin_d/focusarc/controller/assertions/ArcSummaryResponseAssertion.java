package com.valentin_d.focusarc.controller.assertions;

import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import org.springframework.test.web.servlet.ResultActions;

public class ArcSummaryResponseAssertion extends BaseAssertion<ArcSummaryResponseDto> {
    @Override
    protected void assertJson(final ResultActions actions, final String path,
                              final ArcSummaryResponseDto expected) throws Exception {
        expect(actions, path, "arcId", expected.arcId().id().toString());
        expect(actions, path, "name", expected.name());
        expect(actions, path, "totalEstimatedMinutes", expected.totalEstimatedMinutes());
        expect(actions, path, "totalCompletedMinutes", expected.totalCompletedMinutes());
        expect(actions, path, "remainingMinutes", expected.remainingMinutes());
        expect(actions, path, "nbChapterCompleted", expected.nbChapterCompleted());
        expect(actions, path, "nbChapterPlanned", expected.nbChapterPlanned());
        expect(actions, path, "nbChapterSkipped", expected.nbChapterSkipped());
        expect(actions, path, "daysStreak", expected.daysStreak());
    }
}