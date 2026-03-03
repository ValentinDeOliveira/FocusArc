package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseChapterControllerIntegrationTest;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerId;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.assertj.core.api.CollectionAssert.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChapterControllerIntegrationTest extends BaseChapterControllerIntegrationTest {
    @Autowired
    private UserRepository userRepository;

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
    void shouldReturnBadRequestOnCreate_whenArcChapterAlreadyExistForDate() {
        final var date = LocalDate.now().plusDays(5);
        final var arc = createArc();
        chapterRepository.save(aChapterWithScheduledDateAndArcId(date, arc.getId()));

        final var dto = aChapterCreationDtoWithArcIdAndScheduledDate(arc.getId(), date);

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertBadRequest(response);
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
        final var now = LocalDate.now();
        final var chapter1 = createChapterForArcWithDate(arc.getId(), now.plusDays(5));
        final var chapter2 = createChapterForArcWithDate(arc.getId(), now.plusDays(9));

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
    void shouldReturnNoContent_whenArcHasNoChapters() {
        final var arc = createArc();
        final var response = request(URL + "/arcs/" + arc.getId().id(), HttpMethod.GET, Void.class);

        assertNoContent(response);
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
        assertEquals(chapter.getEstimatedMinutes(), result.getEstimatedMinutes());
        assertEquals(chapter.getCompletedMinutes(), result.getCompletedMinutes());
        assertEquals(expectedValue(dto.scheduledDate(), chapter.getScheduledDate()), result.getScheduledDate());
    }

    private static Stream<Arguments> provideChapterUpdateDtos() {
        return Stream.of(
                Arguments.of(aChapterUpdateDtoWithScheduledDate(LocalDate.now().plusDays(10))),
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
        final var now = LocalDate.now();
        createChapterForArcWithDate(arc.getId(), now.plusDays(5));
        createChapterForArcWithDate(arc.getId(), now.plusDays(9));

        final var response = request(URL + "/arcs/" + arc.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllChaptersForNonExistingArc() {
        final var response = request(URL + "/arcs/" + ChapterId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnChapterSummary_whenChapterExists() {
        final var user = userRepository.save(aUser());
        final var arc = arcRepository.save(anArcWithOwnerId(user.getId()));
        final var chapter = createChapterForArcWithDate(arc.getId(), LocalDate.now());
        final var task1 = createTaskForChapterWithStatus(chapter.getId(), TaskStatus.PLANNED);
        final var task2 = createTaskForChapterWithStatus(chapter.getId(), TaskStatus.IN_PROGRESS);
        createTaskForChapterWithStatus(chapter.getId(), TaskStatus.DONE);
        createTaskForChapterWithStatus(chapter.getId(), TaskStatus.DONE);

        final var response = exchangeSummaryForUser(user);

        assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertThatCollection(result.tasksToComplete()).containsExactly(task1, task2);
        assertEquals(result.estimatedMinutes(), chapter.getEstimatedMinutes());
        final var completedMinutes = task1.getCompletedMinutes() +  task2.getCompletedMinutes();
        assertEquals(result.completedMinutes(), completedMinutes);
        assertEquals(result.remainingTime(),  chapter.getEstimatedMinutes() - completedMinutes);
    }

    @Test
    void shouldReturnChapterSummary_whenNoTaskScheduled() {
        final var user = userRepository.save(aUser());
        final var arc = arcRepository.save(anArcWithOwnerId(user.getId()));
        final var chapter = createChapterForArcWithDate(arc.getId(), LocalDate.now());
        createTaskForChapterWithStatus(chapter.getId(), TaskStatus.DONE);
        createTaskForChapterWithStatus(chapter.getId(), TaskStatus.DONE);

        final var response = exchangeSummaryForUser(user);

        assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(0, result.tasksToComplete().size());
        assertEquals(result.estimatedMinutes(), chapter.getEstimatedMinutes());
        assertEquals(0, result.completedMinutes());
        assertEquals(result.remainingTime(),  chapter.getEstimatedMinutes());
    }
}