package com.valentin_d.focusarc.fixtures.arc;

import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import lombok.Builder;

@Builder
public class ArcBuilder {
    @Builder.Default
    private final ArcId id = ArcId.random();
    @Builder.Default
    private final UserId owner = UserId.random();
    @Builder.Default
    private final String name = "Arc 1";
    @Builder.Default
    private final int totalEstimatedMinutes = 120;
    @Builder.Default
    private final ArcStatus status = ArcStatus.ACTIVE;

    public Arc build() {
        return new Arc(id, owner, name, totalEstimatedMinutes, status);
    }
}