package com.valentin_d.focusarc.service.seed;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.tag.Tag;
import com.valentin_d.focusarc.model.tag.TagColor;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.model.user.AuthProvider;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class SeedService {

    public static final String SEED_EMAIL = "dev@focusarc.com";
    public static final String SEED_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final ArcRepository arcRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    public User doSeed() {
        cleanupSeededUser();

        final var user = new User("Dev User", SEED_EMAIL, passwordEncoder.encode(SEED_PASSWORD), AuthProvider.LOCAL);
        userRepository.save(user);

        final var tagWork     = tagRepository.save(new Tag(user.getId(), "Work",     TagColor.BLUE)).getId();
        final var tagStudy    = tagRepository.save(new Tag(user.getId(), "Study",    TagColor.PURPLE)).getId();
        final var tagHealth   = tagRepository.save(new Tag(user.getId(), "Health",   TagColor.TEAL)).getId();
        final var tagPersonal = tagRepository.save(new Tag(user.getId(), "Personal", TagColor.GREEN)).getId();
        final var tagCreative = tagRepository.save(new Tag(user.getId(), "Creative", TagColor.ORANGE)).getId();
        final var tagReading  = tagRepository.save(new Tag(user.getId(), "Reading",  TagColor.YELLOW)).getId();
        final var tagSocial   = tagRepository.save(new Tag(user.getId(), "Social",   TagColor.PINK)).getId();

        final LocalDate now = LocalDate.now();

        final var arc = new Arc(user.getId(), "Spring 2026 Arc", 0,
                now.minusWeeks(2), now.plusWeeks(4));
        arcRepository.save(arc);

        seedDay(arc.getId(), now.minusDays(5), tagCreative, tagReading);
        seedDay(arc.getId(), now.minusDays(3), tagWork, tagHealth);
        seedDay(arc.getId(), now.minusDays(2), tagWork, tagStudy);
        seedDay(arc.getId(), now.minusDays(1), tagWork, tagPersonal);
        seedDay(arc.getId(), now, tagWork, tagStudy);
        seedDay(arc.getId(), now.plusDays(1),   tagWork, tagPersonal);
        seedDay(arc.getId(), now.plusDays(4),   tagSocial, tagCreative);

        recalculateArc(arc);

        return user;
    }

    private void cleanupSeededUser() {
        final var seededUser = userRepository.findByEmail(SEED_EMAIL);
        if (seededUser.isEmpty()) {
            return;
        }
        final var userId = seededUser.get().getId();

        arcRepository.findAllByOwner(userId).forEach(arc -> {
            chapterRepository.findAllByArc(arc.getId()).forEach(chapter ->
                taskRepository.deleteAllByChapter(chapter.getId())
            );
            chapterRepository.deleteAllByArc(arc.getId());
        });

        arcRepository.deleteAllByOwner(userId);
        tagRepository.deleteAllByOwner(userId);
        userRepository.deleteById(userId);
    }

    private void seedDay(final ArcId arcId, final LocalDate date,
                         final TagId primaryTag, final TagId secondaryTag) {
        final var chapter = new Chapter(arcId, 0, date);
        final Instant dayStart = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        final ChapterId chapterId = chapter.getId();
        final LocalDate now = LocalDate.now();

        final List<Task> tasks;
        if (date.equals(now.minusDays(2))) {
            tasks = buildPastTasksWithNotDone(chapterId, dayStart, primaryTag, secondaryTag);
        } else if (date.equals(now.minusDays(1))) {
            tasks = buildYesterdayTasks(chapterId, dayStart, primaryTag, secondaryTag);
        } else if (date.equals(now)) {
            tasks = buildTodayTasks(chapterId, dayStart.plus(8, ChronoUnit.HOURS), primaryTag, secondaryTag);
        } else if (date.isBefore(now)) {
            tasks = buildPastTasks(chapterId, dayStart, primaryTag, secondaryTag);
        } else {
            tasks = buildFutureTasks(chapterId, dayStart, primaryTag, secondaryTag);
        }

        taskRepository.saveAll(tasks);
        chapter.recalculateEstimatedMinutes(tasks);
        chapter.recalculateCompletedMinutes(tasks);
        chapterRepository.save(chapter);
    }

    private List<Task> buildPastTasks(final ChapterId chapterId, final Instant dayStart,
                                      final TagId primary, final TagId secondary) {
        final var t1 = doneTask(chapterId, "Deep work session", 90, 88,  dayStart,                                  primary);
        final var t2 = doneTask(chapterId, "Code review",       30, 25,  dayStart.plus(90,  ChronoUnit.MINUTES),   primary);
        final var t3 = doneTask(chapterId, "Lunch walk",        30, 30,  dayStart.plus(120, ChronoUnit.MINUTES),   secondary);
        final var t4 = doneTask(chapterId, "Gym",               90, 100, dayStart.plus(150, ChronoUnit.MINUTES),   secondary);
        return List.of(t1, t2, t3, t4);
    }

    private List<Task> buildYesterdayTasks(final ChapterId chapterId, final Instant dayStart,
                                           final TagId primary, final TagId secondary) {
        final var tasks = new ArrayList<>(buildPastTasks(chapterId, dayStart, primary, secondary));
        tasks.add(skippedTask(chapterId, "Deep work session", 90, dayStart.plus(3, ChronoUnit.HOURS), primary));
        return tasks;
    }

    private List<Task> buildPastTasksWithNotDone(final ChapterId chapterId, final Instant dayStart,
                                                 final TagId primary, final TagId secondary) {
        final var tasks = new ArrayList<>(buildPastTasks(chapterId, dayStart, primary, secondary));
        tasks.add(plannedTask(chapterId, "Deep work session", 90, dayStart.plus(3, ChronoUnit.HOURS), primary));
        return tasks;
    }

    private List<Task> buildTodayTasks(final ChapterId chapterId, final Instant dayStart,
                                       final TagId primary, final TagId secondary) {
        final var t1 = doneTask(chapterId,    "Cleaning",          50, 43, dayStart.minus(2,   ChronoUnit.HOURS),   primary);
        final var t2 = plannedTask(chapterId, "Morning planning",  15,     dayStart,                                primary);
        final var t3 = plannedTask(chapterId, "Core feature dev",  120,    dayStart.plus(15,  ChronoUnit.MINUTES),  primary);
        final var t4 = plannedTask(chapterId, "Lunch break walk",  30,     dayStart.plus(135, ChronoUnit.MINUTES),  secondary);
        final var t5 = plannedTask(chapterId, "Study session",     60,     dayStart.plus(165, ChronoUnit.MINUTES),  secondary);
        return List.of(t1, t2, t3, t4, t5);
    }

    private List<Task> buildFutureTasks(final ChapterId chapterId, final Instant dayStart,
                                        final TagId primary, final TagId secondary) {
        final var t1 = plannedTask(chapterId, "Architecture review", 90, dayStart,                               primary);
        final var t2 = plannedTask(chapterId, "Personal errands",    45, dayStart.plus(90, ChronoUnit.MINUTES),  secondary);
        return List.of(t1, t2);
    }

    private Task doneTask(final ChapterId chapterId, final String name, final int estimated,
                          final int completed, final Instant startAt, final TagId tag) {
        final var task = new Task(chapterId, estimated, startAt, name, tag);
        task.setStatus(TaskStatus.DONE);
        task.setCompletedMinutes(completed);
        return task;
    }

    private Task skippedTask(final ChapterId chapterId, final String name, final int estimated,
                             final Instant startAt, final TagId tag) {
        final var task = new Task(chapterId, estimated, startAt, name, tag);
        task.setStatus(TaskStatus.SKIPPED);
        return task;
    }

    private Task plannedTask(final ChapterId chapterId, final String name, final int estimated,
                             final Instant startAt, final TagId tag) {
        return new Task(chapterId, estimated, startAt, name, tag);
    }

    private void recalculateArc(final Arc arc) {
        final var chapters = chapterRepository.findAllByArc(arc.getId());
        arc.recalculateEstimatedMinutes(chapters);
        arc.recalculateCompletedMinutes(chapters);
        arcRepository.save(arc);
    }
}