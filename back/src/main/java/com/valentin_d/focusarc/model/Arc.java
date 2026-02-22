package com.valentin_d.focusarc.model;

import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("arcs")
public class Arc {
    @Id
    private ArcId id;
    private UserId owner;
    private String name;
    private int totalPlannedMinutes;
    private int totalCompletedMinutes;

    public Arc(final ArcId arcId, final UserId userId, final String name, final int totalPlannedMinutes) {
        this(arcId, userId, name, totalPlannedMinutes, 0);
    }

    public Arc(final UserId userId, final String name, final int totalPlannedMinutes) {
        this(ArcId.random(), userId, name, totalPlannedMinutes);
    }
}