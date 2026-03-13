package com.valentin_d.focusarc.model.arc;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
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
@Document("arcs")
@CompoundIndexes({
        @CompoundIndex(
                name = "one_active_arc_per_user",
                def = "{'owner.id': 1, 'status': 1}",
                unique = true,
                partialFilter = "{'status': {'$eq': 'ACTIVE'}}"
        )
})
public class Arc {
    @Id
    private ArcId id;
    private UserId owner;
    private String name;
    private int totalEstimatedMinutes;
    private int totalCompletedMinutes;
    private ArcStatus status;
    private LocalDate startDate;
    private LocalDate endDate;

    public Arc(final ArcId arcId, final UserId userId, final String name, final int totalEstimatedMinutes,
               final ArcStatus status, final LocalDate startDate, final LocalDate endDate) {
        this(arcId, userId, name, totalEstimatedMinutes, 0, status, startDate, endDate);
    }

    public Arc(final ArcId arcId, final UserId userId, final String name, final int totalEstimatedMinutes,
               final LocalDate startDate, final LocalDate endDate) {
        this(arcId, userId, name, totalEstimatedMinutes, 0, ArcStatus.ACTIVE, startDate, endDate);
    }

    public Arc(final UserId userId, final String name, final int totalEstimatedMinutes,
               final LocalDate startDate, final LocalDate endDate) {
        this(ArcId.random(), userId, name, totalEstimatedMinutes, startDate, endDate);
    }

    public void recalculateCompletedMinutes(final List<Chapter> chapters) {
        this.totalCompletedMinutes = chapters.stream().mapToInt(Chapter::getCompletedMinutes).sum();
    }

    public void recalculateEstimatedMinutes(final List<Chapter> chapters) {
        this.totalEstimatedMinutes = chapters.stream().mapToInt(Chapter::getEstimatedMinutes).sum();
    }
}