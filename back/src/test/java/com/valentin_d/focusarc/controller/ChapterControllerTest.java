package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.ChapterAssertion;
import com.valentin_d.focusarc.controller.assertions.ChapterSummaryResponseAssertion;
import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTask;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChapterController.class)
class ChapterControllerTest extends BaseSecurityControllerTest {
    @MockitoBean
    private ChapterService chapterService;
    private final static String ROOT = "/chapters";
    private final ChapterAssertion chapterAssertion = new ChapterAssertion();
    private final ChapterSummaryResponseAssertion chapterSummaryResponseAssertion = new ChapterSummaryResponseAssertion();

    @Test
    void shouldReturnChapter_whenIdExists() throws Exception {
        final var chapter = aChapter();
        when(chapterService.findById(chapter.getId(), user.getId())).thenReturn(chapter);

        final var actions = mvcGetWithUser(chapterUrl(chapter.getId()), user)
                .andExpect(status().isOk());

        chapterAssertion.assertSingleJson(actions, chapter);
    }

    @Test
    void shouldReturnNotFoundOnGetById_whenIdDoesNotExists() throws Exception {
        final var chapter = aChapter();

        when(chapterService.findById(chapter.getId(), user.getId()))
                .thenThrow(new ChapterDoesNotExistException(chapter.getId()));

        mvcGetWithUser(chapterUrl(chapter.getId()),  user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfChapter_whenArcIdExists() throws Exception {
        final var chapter = aChapter();
        when(chapterService.findAllForArc(chapter.getArc(), user.getId())).thenReturn(List.of(chapter));

        final var actions = mvcGetWithUser(chapterByArcUrl(chapter.getArc()), user)
                .andExpect(status().isOk());

        chapterAssertion.assertListJson(actions, chapter);
    }

    @Test
    void shouldReturnNoContent_whenArcHasNoChapters() throws Exception {
        when(chapterService.findAllForArc(any(ArcId.class), eq(user.getId()))).thenReturn(List.of());

        mvcGetWithUser(chapterByArcUrl(ArcId.random()), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFound_whenArcDoesNotExist() throws Exception {
        when(chapterService.findAllForArc(any(ArcId.class), eq(user.getId()))).thenThrow(new ArcDoesNotExistException(ArcId.random()));

        mvcGetWithUser(chapterByArcUrl(ArcId.random()), user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateChapter_whenDataIsValid() throws Exception {
        final var chapter = aChapter();
        final var creationDto = aChapterCreationDto();

        when(chapterService.create(any(ChapterCreationDto.class), eq(user.getId()))).thenReturn(chapter);

        final var json = toJson(creationDto);

        final var actions = mvcPostWithUser(ROOT, json, user)
                .andExpect(status().isCreated());

        chapterAssertion.assertSingleJson(actions, chapter);
    }

    @Test
    void shouldReturnChapter_whenUpdatingExistingChapter() throws Exception {
        final var chapter = aChapter();
        final var updateDto = aChapterUpdateDto();

        when(chapterService.update(eq(chapter.getId()), any(UserId.class), eq(updateDto)))
                .thenReturn(chapter);

        final String json = toJson(updateDto);

        final var actions = mvcPutWithUser(chapterUrl(chapter.getId()), json, user)
                .andExpect(status().isOk());

        chapterAssertion.assertSingleJson(actions, chapter);
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingChapter() throws Exception {
        final var chapter = aChapter();

        mvcDeleteWithUser(chapterUrl(chapter.getId()), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllChapterForExistingArc() throws Exception {
        final var chapter = aChapter();

        mvcDeleteWithUser(chapterByArcUrl(chapter.getArc()), user)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnSummary_whenGettingSummary() throws Exception {
        final var task = aTask();
        final var summary = aChapterSummaryResponseDtoWithTasks(List.of(task));

        when(chapterService.getChapterSummary(user.getId())).thenReturn(summary);
        final var actions = mvcGetWithUser(ROOT + "/summary", user)
                .andExpect(status().isOk());

        chapterSummaryResponseAssertion.assertSingleJson(actions, summary);
    }

    private String chapterUrl(ChapterId chapterId) {
        return ROOT + "/" + chapterId.id();
    }

    private String chapterByArcUrl(ArcId arcId) {
        return ROOT + "/arcs/" + arcId.id();
    }
}