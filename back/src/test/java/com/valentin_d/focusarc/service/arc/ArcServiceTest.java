package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.dto.tag.TagTaskStatsDto;
import com.valentin_d.focusarc.dto.task.TaskStatsDto;
import com.valentin_d.focusarc.exception.InvalidDateRangeException;
import com.valentin_d.focusarc.exception.arc.ArcAlreadyExistsException;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.exception.arc.NoActiveArcException;
import com.valentin_d.focusarc.exception.user.UserDoesNotExistException;
import com.valentin_d.focusarc.fixtures.arc.ArcBuilder;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.service.task.TaskLoader;
import com.valentin_d.focusarc.service.task.TaskService;
import com.valentin_d.focusarc.service.user.UserLoader;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithScheduledDateAndArcIdAndCompletedMinutesAndAllTasksCompleted;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskRecurrenceDto;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArcServiceTest {
    @Mock
    private ArcRepository arcRepository;
    @Mock
    private ArcLoader arcLoader;
    @Mock
    private UserLoader userLoader;
    @Mock
    private ChapterService chapterService;
    @Mock
    private ChapterLoader chapterLoader;
    @Mock
    private TaskLoader taskLoader;
    @Mock
    private TaskService taskService;
    @InjectMocks
    private ArcService arcService;


    @Test
    void shouldCreateArc_whenUserDoesExist() {
        final var userId = UserId.random();
        final var creationDto = anArcCreationDto();

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = arcService.create(userId, creationDto);

        assertEquals(userId, result.getOwner());
        assertEquals(creationDto.name(), result.getName());
        assertEquals(0, result.getTotalCompletedMinutes());
        assertEquals(0, result.getTotalEstimatedMinutes());

        verify(arcRepository).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenUserNotFound() {
        final var userId = UserId.random();
        final var creationDto = anArcCreationDto();

        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> arcService.create(userId, creationDto))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id().toString()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenAnotherActiveArcExist() {
        final var userId = UserId.random();
        final var creationDto = anArcCreationDto();

        doThrow(new ArcAlreadyExistsException(userId))
                .when(arcLoader)
                .assertNotAnotherActiveArc(eq(userId));

        assertThatThrownBy(() -> arcService.create(userId, creationDto))
                .isInstanceOf(ArcAlreadyExistsException.class)
                .hasMessageContaining(String.valueOf(userId.id().toString()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldUpdate_whenArcExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());
        final var updateDto = anArcUpdateDto();

        when(arcLoader.getArcIfExistsForUser(eq(arc.getId()), eq(user.getId()))).thenReturn(arc);

        when(arcRepository.save(any(Arc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Arc updated = arcService.update(user.getId(), arc.getId(), updateDto);

        verify(arcRepository).save(arc);

        assertEquals(arc.getName(), updated.getName());
        assertEquals(arc.getTotalEstimatedMinutes(), updated.getTotalEstimatedMinutes());
        assertEquals(arc.getId(), updated.getId());
        assertEquals(arc.getOwner(), updated.getOwner());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenDateAreInvalid() {
        final var now = LocalDate.now();
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());
        final var updateDto = anArcUpdateDtoWithStartAndEndDate(now, now.minusDays(15));

        when(arcLoader.getArcIfExistsForUser(eq(arc.getId()), eq(user.getId()))).thenReturn(arc);

        assertThatThrownBy(() -> arcService.update(user.getId(), arc.getId(), updateDto))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenArcDoesNotExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());
        final var updateDto = anArcUpdateDto();

        doThrowArcDoesNotExistForUser(arc.getId(), user.getId());

        assertThatThrownBy(() -> arcService.update(user.getId(), arc.getId(), updateDto))
                .isInstanceOf(ArcDoesNotExistForUserException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldDeleteArc_whenArcExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        when(arcLoader.getArcIfExistsForUser(arc.getId(), user.getId())).thenReturn(arc);

        arcService.delete(user.getId(), arc.getId());

        verify(arcRepository).delete(arc);
        verify(chapterService).deleteAllForArc(arc.getId(), user.getId());
    }

    @Test
    void shouldThrowExceptionOnDelete_whenArcDoesNotExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        doThrowArcDoesNotExistForUser(arc.getId(), user.getId());

        assertThatThrownBy(() -> arcService.delete(user.getId(), arc.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(arcRepository, never()).delete(any(Arc.class));
    }

    @Test
    void shouldDeleteAllArcs_whenUserExists() {
        final var user = aUser();
        final var arc = anArcWithOwnerId(user.getId());

        when(arcLoader.getAllByOwner(user.getId())).thenReturn(List.of(arc));

        arcService.deleteAllForUser(user.getId());

        verify(arcRepository).deleteAll(List.of(arc));
        verify(chapterService).deleteAllForArc(arc.getId(), user.getId());
    }

    @Test
    void shouldThrowExceptionOnDeleteAllArcs_whenUserDoesNotExists() {
        final var userId = UserId.random();

        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> arcService.deleteAllForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(arcRepository, never()).deleteAll(anyList());
    }

    @Test
    void shouldGetAllArcsForUser_whenUserExists() {
        final var arc = anArc();
        when(arcLoader.getAllByOwner(arc.getOwner())).thenReturn(List.of(arc));

        final var result = arcService.findAllForUser(arc.getOwner());

        assertEquals(List.of(arc), result);
    }

    @Test
    void shouldThrowExceptionOnGetAllArcsForUser_whenUserDoesNotExists() {
        final var arc = anArc();

        doThrowUserDoesNotExist(arc.getOwner());

        assertThatThrownBy(() -> arcService.findAllForUser(arc.getOwner()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getOwner().id()));

        verify(arcRepository, never()).findAllByOwner(any(UserId.class));
    }

    @Test
    void shouldGetActiveArcForUser_whenUserExists() {
        final var arc = anArc();
        when(arcLoader.findActiveArcForUser(arc.getOwner())).thenReturn(Optional.of(arc));

        final var result = arcService.findActiveArcForUser(arc.getOwner());

        assertTrue(result.isPresent());
        assertEquals(arc, result.get());
    }

    @Test
    void shouldThrowExceptionOnGetActiveArcForUser_whenUserDoesNotExists() {
        final var arc = anArc();

        doThrowUserDoesNotExist(arc.getOwner());

        assertThatThrownBy(() -> arcService.findActiveArcForUser(arc.getOwner()))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(arc.getOwner().id()));

        verify(arcLoader, never()).findActiveArcForUser(any(UserId.class));
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenUserDoesNotOwnArc() {
        final var owner = aUser();
        final var attacker = aUser();
        final var arc = anArcWithOwnerId(owner.getId());
        final var updateDto = anArcUpdateDto();

        doThrowArcDoesNotExistForUser(arc.getId(), attacker.getId());

        assertThatThrownBy(() -> arcService.update(attacker.getId(), arc.getId(), updateDto))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(arcRepository, never()).save(any(Arc.class));
    }

    @Test
    void shouldThrowExceptionOnDelete_whenUserDoesNotOwnArc() {
        final var owner = aUser();
        final var attacker = aUser();
        final var arc = anArcWithOwnerId(owner.getId());

        doThrowArcDoesNotExistForUser(arc.getId(), attacker.getId());

        assertThatThrownBy(() -> arcService.delete(attacker.getId(), arc.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(arcRepository, never()).delete(any(Arc.class));
    }

    @Test
    void shouldReturnSummary_whenUserHasActiveArc() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerIdAndCompletedMinutes(userId, 60);

        final var now = LocalDate.now();

        final var yesterday = aChapterWithScheduledDateAndArcIdAndCompletedMinutesAndAllTasksCompleted(
                now.minusDays(1), arc.getId(), 60, true);
        final var threeDaysAgo = aChapterWithScheduledDateAndArcIdAndCompletedMinutesAndAllTasksCompleted(
                now.minusDays(3), arc.getId(), 0, false);
        final var tomorrow = aChapterWithScheduledDateAndArcIdAndCompletedMinutesAndAllTasksCompleted(
                now.plusDays(1), arc.getId(), 0, false);

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);
        when(chapterLoader.findAllByArc(arc.getId())).thenReturn(List.of(yesterday, threeDaysAgo, tomorrow));

        final var result = arcService.getSummaryForUser(userId);

        assertThat(result.totalEstimatedMinutes()).isEqualTo(arc.getTotalEstimatedMinutes());
        assertThat(result.totalCompletedMinutes()).isEqualTo(60);
        assertThat(result.remainingMinutes()).isEqualTo(arc.getTotalEstimatedMinutes() - 60);
        assertThat(result.nbChapterCompleted()).isEqualTo(1);
        assertThat(result.nbChapterSkipped()).isEqualTo(1);
        assertThat(result.nbChapterPlanned()).isEqualTo(1);
        assertThat(result.daysStreak()).isEqualTo(1);
    }

    @Test
    void shouldThrowException_whenUserDoesNotExist_onGetSummary() {
        final var userId = UserId.random();
        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> arcService.getSummaryForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(arcLoader, never()).getActiveArcForUser(any());
    }

    @Test
    void shouldThrowException_whenNoActiveArc_onGetSummary() {
        final var userId = UserId.random();
        doThrow(new NoActiveArcException(userId)).when(arcLoader).getActiveArcForUser(userId);

        assertThatThrownBy(() -> arcService.getSummaryForUser(userId))
                .isInstanceOf(NoActiveArcException.class);

        verify(chapterLoader, never()).findAllByArc(any());
    }

    @Test
    void shouldReturnTagStats_whenUserHasActiveArc() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerIdAndCompletedMinutes(userId, 60);

        final var chapter1 = aChapterWithArcId(arc.getId());
        final var chapter2 = aChapterWithArcId(arc.getId());

        final var expectedStats = List.of(
                new TagTaskStatsDto(TagId.random(), 3L, 2L),
                new TagTaskStatsDto(TagId.random(), 1L, 0L)
        );

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);
        when(chapterLoader.findAllByArc(arc.getId())).thenReturn(List.of(chapter1, chapter2));
        when(taskLoader.getTagStatsForChapters(List.of(chapter1.getId(), chapter2.getId())))
                .thenReturn(expectedStats);

        final var result = arcService.getTagTaskStats(userId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedStats);
    }

    @ParameterizedTest
    @MethodSource("statsMethodsProvider")
    void shouldThrowException_whenUserDoesNotExist_onGetChaptersForUser(BiConsumer<ArcService, UserId> statsMethod) {
        final var userId = UserId.random();
        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> statsMethod.accept(arcService, userId))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(arcLoader, never()).getActiveArcForUser(any());
    }

    @ParameterizedTest
    @MethodSource("statsMethodsProvider")
    void shouldThrowException_whenNoActiveArc_onGetChaptersForUser(BiConsumer<ArcService, UserId> statsMethod) {
        final var userId = UserId.random();
        doThrow(new NoActiveArcException(userId)).when(arcLoader).getActiveArcForUser(userId);

        assertThatThrownBy(() -> statsMethod.accept(arcService, userId))
                .isInstanceOf(NoActiveArcException.class);

        verify(chapterLoader, never()).findAllByArc(any());
    }

    private static Stream<Arguments> statsMethodsProvider() {
        return Stream.of(
                Arguments.of(Named.of("getTagTaskStats", (BiConsumer<ArcService, UserId>) ArcService::getTagTaskStats)),
                Arguments.of(Named.of("getTaskStats", (BiConsumer<ArcService, UserId>) ArcService::getTaskStats))
        );
    }

    @Test
    void shouldReturnTaskStats_whenUserHasActiveArc() {
        final var userId = UserId.random();
        final var arc = anArcWithOwnerIdAndCompletedMinutes(userId, 60);

        final var chapter1 = aChapterWithArcId(arc.getId());
        final var chapter2 = aChapterWithArcId(arc.getId());

        final var expectedStats = List.of(
                new TaskStatsDto(TaskStatus.DONE, 5L, 5L),
                new TaskStatsDto(TaskStatus.SKIPPED, 2L, 0L)
        );

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);
        when(chapterLoader.findAllByArc(arc.getId())).thenReturn(List.of(chapter1, chapter2));
        when(taskLoader.getTaskStatsForChapters(List.of(chapter1.getId(), chapter2.getId())))
                .thenReturn(expectedStats);

        final var result = arcService.getTaskStats(userId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedStats);
    }


    @Test
    void shouldThrowException_whenArcDoesNotExist_onMassCreate() {
        final var userId = UserId.random();
        final var arcId = ArcId.random();

        doThrowArcDoesNotExistForUser(arcId, userId);

        assertThatThrownBy(() -> arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), arcId, userId))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(chapterLoader, never()).findAllByArc(any());
    }

    @ParameterizedTest
    @MethodSource("provideArcWithMissingDate")
    void shouldThrowException_whenDateIsInvalid_onMassCreate(@NotNull final Arc arc) {
        when(arcLoader.getArcIfExistsForUser(arc.getId(), arc.getOwner())).thenReturn(arc);

        assertThatThrownBy(() -> arcService.massCreate(List.of(aTaskRecurrenceDto(new TaskRecurrence.Daily())), arc.getId(), arc.getOwner()))
                .isInstanceOf(IllegalStateException.class);

        verify(chapterLoader, never()).findAllByArc(any());
    }

    private static Stream<Arc> provideArcWithMissingDate() {
        return Stream.of(
                ArcBuilder.builder().owner(UserId.random()).startDate(null).build().build(),
                ArcBuilder.builder().owner(UserId.random()).endDate(null).build().build()
        );
    }

    private void doThrowArcDoesNotExistForUser(final ArcId arcId, final UserId userId) {
        doThrow(new ArcDoesNotExistForUserException(arcId, userId))
                .when(arcLoader)
                .getArcIfExistsForUser(arcId, userId);
    }

    private void doThrowUserDoesNotExist(final UserId userId) {
        doThrow(new UserDoesNotExistException(userId))
                .when(userLoader)
                .assertUserExists(eq(userId));
    }

}