package com.valentin_d.focusarc.model;

import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("chapters")
public class Chapter {
    @Id
    private ChapterId id;
    private ArcId arc;
    // TODO planned or estimated?
    private int plannedMinutes;
    private int completedMinutes;

    public Chapter(final ChapterId chapterId, final ArcId arc, final int plannedMinutes) {
        this(chapterId, arc, plannedMinutes, 0);
    }

    public Chapter(final ArcId arc, final int plannedMinutes) {
        this(ChapterId.random(), arc, plannedMinutes);
    }

    public void recalculateCompletedMinutes(final List<Task> tasks) {
        this.completedMinutes = tasks.stream().mapToInt(Task::getCompletedMinutes).sum();
    }

    public void recalculateEstimatedMinutes(final List<Task> tasks) {
        this.plannedMinutes = tasks.stream().mapToInt(Task::getEstimatedMinutes).sum();
    }
}