package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseChapterControllerIntegrationTest;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static org.assertj.core.api.CollectionAssert.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChapterControllerIntegrationTest extends BaseChapterControllerIntegrationTest {
    @Test
    void shouldCreateChapter_whenDataIsValid() {
        final var arc = createArc();

        final var dto = aChapterCreationDtoWithArcId(arc.getId());
        final var response = request(URL, HttpMethod.POST, dto, Chapter.class);

        assertCreated(response);

        final Chapter chapter = response.getBody();
        assertNotNull(chapter);
        assertEquals(dto.estimatedMinutes(), chapter.getEstimatedMinutes());
        assertEquals(dto.arcId(), chapter.getArc());
        assertEquals(0, chapter.getCompletedMinutes());
        assertNotNull(chapter.getId());
    }

    @Test
    void shouldReturnNotFoundOnCreate_whenArcDoesNotExists() {
        final var dto = aChapterCreationDto();

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnChapter_whenIdExists() {
        final var chapter = createChapter();

        final var response = request(URL + "/" + chapter.getId().id(), HttpMethod.GET, Chapter.class);
        assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertChaptersEquals(result, chapter);
    }

    @Test
    void shouldReturnAllChapter_whenArcIdExists() {
        final var arc = createArc();
        final var chapter1 = createChapterForArc(arc.getId());
        final var chapter2 = createChapterForArc(arc.getId());

        final var response = request(URL + "/arcs/" + arc.getId().id(), HttpMethod.GET, Chapter[].class);
        assertOk(response);
        assertNotNull(response.getBody());

        final List<Chapter> arcs = Arrays.stream(response.getBody()).toList();
        assertNotNull(arcs);
        assertEquals(2, arcs.size());
        assertThatCollection(arcs).containsExactly(chapter1, chapter2);
    }

    @Test
    void shouldReturnNotFound_whenArcIdDoesNotExists() {
        final var response = request(URL + "/arcs/" + ArcId.random().id(), HttpMethod.GET, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnNotFound_whenArcIdExistsWithoutChapter() {
        final var arc = createArc();
        final var response = request(URL + "/arcs/" + arc.getId().id(), HttpMethod.GET, Void.class);

        assertNotFound(response);
    }

    @ParameterizedTest
    @MethodSource("provideChapterUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final ChapterUpdateDto dto) {
        final var chapter = createChapter();

        final var response = request(URL + "/" + chapter.getId().id(), HttpMethod.PUT, dto, Chapter.class);

        assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(chapter.getId(), result.getId());
        assertEquals(chapter.getArc(), result.getArc());
        assertEquals(expectedValue(dto.completedMinutes(), chapter.getCompletedMinutes()), result.getCompletedMinutes());
        assertEquals(expectedValue(dto.estimatedMinutes(), chapter.getEstimatedMinutes()), result.getEstimatedMinutes());
    }

    private static Stream<Arguments> provideChapterUpdateDtos() {
        return Stream.of(
                Arguments.of(aChapterUpdateDtoWithCompletedMinutesAndEstimatedMinutes(200, 250)),
                Arguments.of(aChapterUpdateDtoWithEstimatedMinutes(240)),
                Arguments.of(aChapterUpdateDtoWithCompletedMinutes(50)),
                Arguments.of(aChapterUpdateDtoWithNullFields())
        );
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingChapter() {
        final var dto = aChapterUpdateDto();

        final var response = request(URL + "/" + ChapterId.random().id(), HttpMethod.PUT, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteChapter_whenIdExists() {
        final var chapter = createChapter();

        final var response = request(URL + "/" + chapter.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingChapter() {
        final var response = request(URL + "/" + ChapterId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteAllChaptersForArc_whenArcIdExists() {
        final var arc = createArc();
        createChapterForArc(arc.getId());
        createChapterForArc(arc.getId());

        final var response = request(URL + "/arcs/" + arc.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllChaptersForNonExistingArc() {
        final var response = request(URL + "/arcs/" + ChapterId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }
}