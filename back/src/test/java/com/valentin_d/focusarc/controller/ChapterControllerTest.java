package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.ChapterAssertion;
import com.valentin_d.focusarc.controller.assertions.ChapterSummaryResponseAssertion;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTask;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChapterController.class)
class ChapterControllerTest extends BaseControllerTest {
    @MockitoBean
    private ChapterService chapterService;
    private final static String ROOT = "/chapters";
    private final ChapterAssertion chapterAssertion = new ChapterAssertion();
    private final ChapterSummaryResponseAssertion chapterSummaryResponseAssertion = new ChapterSummaryResponseAssertion();
    @Test
    void shouldReturnChapter_whenIdExists() throws Exception {
        final var chapter = aChapter();
        when(chapterService.findById(chapter.getId())).thenReturn(Optional.of(chapter));

        final var actions = mvcGet(ROOT + "/" + chapter.getId().id())
                .andExpect(status().isOk());

        chapterAssertion.assertSingleJson(actions, chapter);
    }

    @Test
    void shouldReturnNotFoundOnGetById_whenIdDoesNotExists() throws Exception {
        final var chapter = aChapter();
        when(chapterService.findById(chapter.getId())).thenReturn(Optional.empty());

        mvcGet(ROOT + "/" + chapter.getId().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfChapter_whenArcIdExists() throws Exception {
        final var chapter = aChapter();
        when(chapterService.findAllForArc(chapter.getArc())).thenReturn(List.of(chapter));

        final var actions = mvcGet(ROOT + "/arcs/" + chapter.getArc().id())
                .andExpect(status().isOk());

        chapterAssertion.assertListJson(actions, chapter);
    }

    @Test
    void shouldReturnNotFound_whenArcIdDoesNotExists() throws Exception {
        when(chapterService.findAllForArc(any())).thenReturn(List.of());

        mvcGet(ROOT + "/arcs/" + ChapterId.random().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateChapter_whenDataIsValid() throws Exception {
        final var chapter = aChapter();
        final var creationDto = aChapterCreationDto();

        when(chapterService.create(any())).thenReturn(chapter);

        final var json = toJson(creationDto);

        final var actions = mvcPost(ROOT, json)
                .andExpect(status().isCreated());

        chapterAssertion.assertSingleJson(actions, chapter);
    }

    @Test
    void shouldReturnChapter_whenUpdatingExistingChapter() throws Exception {
        final var chapter = aChapter();
        final var updateDto = aChapterUpdateDto();

        when(chapterService.update(eq(chapter.getId()), any())).thenReturn(chapter);

        final String json = toJson(updateDto);

        final var actions = mvcPut(ROOT + "/" + chapter.getId().id(), json)
                .andExpect(status().isOk());

        chapterAssertion.assertSingleJson(actions, chapter);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingChapter() throws Exception {
        final var chapter = aChapter();

        mvcDelete(ROOT + "/" + chapter.getId().id())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllChapterForExistingArc() throws Exception {
        final var chapter = aChapter();

        mvcDelete(ROOT + "/arcs/" + chapter.getArc().id())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnSummary_whenGettingSummary() throws Exception {
        final var task = aTask();
        final var summary = aChapterSummaryResponseDtoWithTasks(List.of(task));
        final var userId = UserId.random();

        when(chapterService.getChapterSummary(userId)).thenReturn(summary);
        final var actions = mvcGet(ROOT + "/summary?userId=" + userId.id())
                .andExpect(status().isOk());

        chapterSummaryResponseAssertion.assertSingleJson(actions, summary);
    }
}