package com.valentin_d.focusarc.model;

import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("arcs")
public class Arc {
    @Id
    private ArcId id;
    private UserId owner;
    private String name;
    private int totalEstimatedMinutes;
    private int totalCompletedMinutes;

    public Arc(final ArcId arcId, final UserId userId, final String name, final int totalEstimatedMinutes) {
        this(arcId, userId, name, totalEstimatedMinutes, 0);
    }

    public Arc(final UserId userId, final String name, final int totalEstimatedMinutes) {
        this(ArcId.random(), userId, name, totalEstimatedMinutes);
    }

    public void recalculateCompletedMinutes(final List<Chapter> chapters) {
        this.totalCompletedMinutes = chapters.stream().mapToInt(Chapter::getCompletedMinutes).sum();
    }

    public void recalculateEstimatedMinutes(final List<Chapter> chapters) {
        this.totalEstimatedMinutes = chapters.stream().mapToInt(Chapter::getEstimatedMinutes).sum();
    }
}