package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.fixtures.arc.ArcBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcUpdateDtoBuilder;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.UserId;

public final class ArcFactory {
    private ArcFactory() {}

    public static Arc anArc() {
        return ArcBuilder.builder().build().build();
    }

    public static Arc anArcWithOwnerId(final UserId ownerId) {
        return ArcBuilder.builder().owner(ownerId).build().build();
    }

    public static ArcCreationDto anArcCreationDto() {
        return ArcCreationDtoBuilder.builder().build().build();
    }

    public static ArcCreationDto anArcCreationDtoWithOwnerId(final UserId ownerId) {
        return ArcCreationDtoBuilder.builder().ownerId(ownerId).build().build();
    }

    public static ArcUpdateDto anArcUpdateDto() {
        return ArcUpdateDtoBuilder.builder().build().build();
    }
}