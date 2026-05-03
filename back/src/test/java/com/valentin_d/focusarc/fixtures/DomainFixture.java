package com.valentin_d.focusarc.fixtures;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerAndStartAndEndDates;
import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerIdAndStatus;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.TagFactory.aTagWithOwnerId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;

@Component
@RequiredArgsConstructor
public class DomainFixture {
    private final UserRepository userRepository;
    private final ArcRepository arcRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    public Arc arc() {
        return arcRepository.save(anArc());
    }

    public Arc arcForUser(final UserId userId) {
        return arcForUser(userId, ArcStatus.ACTIVE);
    }

    public Arc arcForUser(final UserId userId, final ArcStatus arcStatus) {
        return arcRepository.save(anArcWithOwnerIdAndStatus(userId, arcStatus));
    }

    public Arc arcForUserWithDates(final UserId userId, final LocalDate start, final LocalDate end) {
        return arcRepository.save(anArcWithOwnerAndStartAndEndDates(userId, start, end));
    }

    public Chapter chapter() {
        return chapterRepository.save(aChapter());
    }

    public Chapter chapterForArc(final ArcId arcId) {
        return chapterRepository.save(aChapterWithArcId(arcId));
    }

    public Chapter chapterForArcWithDate(final ArcId arcId, final LocalDate date) {
        final var chapter = aChapterWithScheduledDateAndArcIdAndAllTasksDone(date, arcId, false);
        return chapterRepository.save(chapter);
    }

    public Chapter chapterForArcWithDateAllTaskDone(final ArcId arcId, final LocalDate date) {
        final var chapter = aChapterWithScheduledDateAndArcIdAndAllTasksDone(date, arcId, true);
        return chapterRepository.save(chapter);
    }

    public Chapter plannedChapterForArcWithDate(final ArcId arcId, final LocalDate date) {
        final var chapter = aChapterWithScheduledDateAndArcIdAndCompletedMinutesAndAllTasksCompleted(date, arcId,
                0, false);
        return chapterRepository.save(chapter);
    }

    public Chapter chapterForUser(final UserId userId) {
        final var arc = arcForUser(userId);
        return chapterForArc(arc.getId());
    }

    public Task taskForChapter(final ChapterId chapterId) {
        final var task = aTaskWithChapterId(chapterId);
        return taskRepository.save(task);
    }

    public Task taskForChapterAtTime(final ChapterId chapterId, final Instant startAt, final int estimatedMinutes) {
        return taskRepository.save(aTaskWithChapterIdAndWindow(chapterId, startAt, estimatedMinutes));
    }

    public Task taskForChapterWithStatus(final ChapterId chapterId, final TaskStatus status) {
        final var task = aTaskWithChapterIdAndStatus(chapterId, status);
        return taskRepository.save(task);
    }

    public Task taskForChapterWithTag(final ChapterId chapterId, final TagId tagId) {
        return taskRepository.save(aTaskWithChapterIdAndTag(chapterId, tagId));
    }

    public Task taskForChapterWithStatusAndTag(final ChapterId chapterId, final TaskStatus status, final TagId tagId) {
        return taskRepository.save(aTaskWithChapterIdAndStatusAndTag(chapterId, status, tagId));
    }

    public Task taskWithChapter() {
        final var chapter = chapter();
        return taskForChapter(chapter.getId());
    }

    public Task taskForUser(final UserId userId) {
        final var chapter = chapterForUser(userId);
        return taskForChapter(chapter.getId());
    }

    public Tag tagForUser(final UserId userId) {
        return tagRepository.save(aTagWithOwnerId(userId));
    }

    public User user() {
        return userRepository.save(aUser());
    }
}