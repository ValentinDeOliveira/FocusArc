package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.dto.chapter.ChapterSummaryResponseDto;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.arc.ArcRecalculationService;
import com.valentin_d.focusarc.service.chapter.ChapterRecalculationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseChapterControllerIntegrationTest extends SecuredIntegrationTest {
    protected final String URL = "/chapters";
    @MockitoBean
    private ChapterRecalculationService chapterRecalculationService;
    @MockitoBean
    private ArcRecalculationService arcRecalculationService;

    protected void assertChaptersEquals(final Chapter expected, final Chapter actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCompletedMinutes(), actual.getCompletedMinutes());
        assertEquals(expected.getEstimatedMinutes(), actual.getEstimatedMinutes());
        assertEquals(expected.getArc(), actual.getArc());
        assertEquals(expected.getScheduledDate(), actual.getScheduledDate());
    }

    protected ResponseEntity<ChapterSummaryResponseDto> exchangeSummaryForUser(final User user) {
        final var headers = getHeadersForUser(user);

        return request(URL + "/summary", HttpMethod.GET, new HttpEntity<>(headers),
                ChapterSummaryResponseDto.class);
    }
}