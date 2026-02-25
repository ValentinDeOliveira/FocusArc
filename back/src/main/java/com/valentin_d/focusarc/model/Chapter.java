package com.valentin_d.focusarc.model;

import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("chapters")
@CompoundIndexes({
        // enforce invariant to assert only ONE CHAPTER per day AT MOST
        @CompoundIndex(name = "arc_date_unique", def = "{'arc.id': 1, 'scheduledDate': 1}", unique = true)
})
public class Chapter {
    @Id
    private ChapterId id;
    private ArcId arc;
    private int estimatedMinutes;
    private int completedMinutes;
    private LocalDate scheduledDate;

    public Chapter(final ChapterId chapterId, final ArcId arc, final int estimatedMinutes,  final LocalDate scheduledDate) {
        this(chapterId, arc, estimatedMinutes, 0, scheduledDate);
    }

    public Chapter(final ChapterId chapterId, final ArcId arc, final int estimatedMinutes) {
        this(chapterId, arc, estimatedMinutes, 0, LocalDate.now());
    }

    public Chapter(final ArcId arc, final int estimatedMinutes) {
        this(ChapterId.random(), arc, estimatedMinutes);
    }

    public void recalculateCompletedMinutes(final List<Task> tasks) {
        this.completedMinutes = tasks.stream().mapToInt(Task::getCompletedMinutes).sum();
    }

    public void recalculateEstimatedMinutes(final List<Task> tasks) {
        this.estimatedMinutes = tasks.stream().mapToInt(Task::getEstimatedMinutes).sum();
    }
}