package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.service.ChapterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChapterController.class)
class ChapterControllerTest extends BaseControllerTest {
    @MockitoBean
    private ChapterService chapterService;

    private final static String ROOT = "/chapters";

    @Test
    void shouldReturnChapter_whenIdExists() throws Exception {
        final var chapter = aChapter();
        when(chapterService.findById(chapter.getId())).thenReturn(Optional.of(chapter));

        final var actions = mvcGet(ROOT + "/" + chapter.getId().id())
                .andExpect(status().isOk());

        assertChapterJson(actions, chapter);
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

        assertChapterListJson(actions, chapter);
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

        assertChapterJson(actions, chapter);
    }

    @Test
    void shouldReturnChapter_whenUpdatingExistingChapter() throws Exception {
        final var chapter = aChapter();
        final var updateDto = aChapterUpdateDto();

        when(chapterService.update(eq(chapter.getId()), any())).thenReturn(chapter);

        final String json = toJson(updateDto);

        final var actions = mvcPut(ROOT + "/" + chapter.getId().id(), json)
                .andExpect(status().isOk());

        assertChapterJson(actions, chapter);
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

    private void assertChapterJson(final ResultActions actions, final Chapter expected) throws Exception {
        assertChapterJson(actions, "$", expected);
    }

    private void assertChapterListJson(final ResultActions actions, final Chapter expected) throws Exception {
        assertChapterJson(actions, "$[0]", expected);
    }

    private void assertChapterJson(final ResultActions actions, final String path, final Chapter expected) throws Exception {
        actions
                .andExpect(jsonPath(path + ".id").value(expected.getId().id().toString()))
                .andExpect(jsonPath(path + ".arc").value(expected.getArc().id().toString()))
                .andExpect(jsonPath(path + ".plannedMinutes").value(expected.getPlannedMinutes()))
                .andExpect(jsonPath(path + ".completedMinutes").value(expected.getCompletedMinutes()));
    }
}