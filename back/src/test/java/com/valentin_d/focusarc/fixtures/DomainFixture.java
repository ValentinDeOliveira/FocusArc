package com.valentin_d.focusarc.fixtures;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerIdAndStatus;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterIdAndStatus;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;

@Component
@RequiredArgsConstructor
public class DomainFixture {
    private final UserRepository userRepository;
    private final ArcRepository arcRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;

    public Arc arc() {
        return arcRepository.save(anArc());
    }

    public Arc arcWithUser() {
        final var user = user();
        return arcForUser(user.getId());
    }

    public Arc arcForUser(final UserId userId) {
        return arcForUser(userId, ArcStatus.ACTIVE);
    }

    public Arc arcForUser(final UserId userId, final ArcStatus arcStatus) {
        return arcRepository.save(anArcWithOwnerIdAndStatus(userId, arcStatus));
    }

    public Chapter chapter() {
        return chapterRepository.save(aChapter());
    }

    public Chapter chapterForArc(final ArcId arcId) {
        return chapterRepository.save(aChapterWithArcId(arcId));
    }

    public Chapter chapterForArcWithDate(final ArcId arcId, final LocalDate date) {
        final var chapter = aChapterWithScheduledDateAndArcId(date, arcId);
        return chapterRepository.save(chapter);
    }

    public Task taskForChapter(final ChapterId chapterId) {
        final var task = aTaskWithChapterId(chapterId);
        return taskRepository.save(task);
    }

    public Task taskForChapterWithStatus(final ChapterId chapterId, final TaskStatus status) {
        final var task = aTaskWithChapterIdAndStatus(chapterId, status);
        return taskRepository.save(task);
    }

    public Task taskWithChapter() {
        final var chapter = chapter();
        return taskForChapter(chapter.getId());
    }

    public Task taskForArc() {
        final var arc = arc();
        final var chapter = chapterForArc(arc.getId());
        return taskForChapter(chapter.getId());
    }

    public User user() {
        return userRepository.save(aUser());
    }
}