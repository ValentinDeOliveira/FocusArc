package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import com.valentin_d.focusarc.fixtures.arc.ArcBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcSummaryResponseBuilder;
import com.valentin_d.focusarc.fixtures.arc.ArcUpdateDtoBuilder;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.UserId;

import java.time.LocalDate;

public final class ArcFactory {
    private ArcFactory() {}

    public static Arc anArc() {
        return ArcBuilder.builder().build().build();
    }

    public static Arc anArcWithOwnerId(final UserId ownerId) {
        return ArcBuilder.builder().owner(ownerId).build().build();
    }

    public static Arc anArcWithOwnerIdAndStatus(final UserId ownerId, final ArcStatus status) {
        return ArcBuilder.builder().owner(ownerId).status(status).build().build();
    }

    public static ArcCreationDto anArcCreationDto() {
        return ArcCreationDtoBuilder.builder().build().build();
    }

    public static ArcCreationDto anArcCreationDtoWithEstimatedMinutes(final int minutes) {
        return ArcCreationDtoBuilder.builder().totalEstimatedMinutes(minutes).build().build();
    }

    public static ArcUpdateDto anArcUpdateDto() {
        return ArcUpdateDtoBuilder.builder().build().build();
    }

    // TODO: refactor to call "allFieldsNull"
    public static ArcUpdateDto anArcUpdateDtoWithNullFields() {
        return ArcUpdateDtoBuilder.builder().totalEstimatedMinutes(null).name(null).startDate(null).endDate(null).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithStartDate(final LocalDate startDate) {
        return ArcUpdateDtoBuilder.builder().name(null).totalEstimatedMinutes(null).endDate(null).startDate(startDate).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithEndDate(final LocalDate endDate) {
        return ArcUpdateDtoBuilder.builder().name(null).totalEstimatedMinutes(null).startDate(null).endDate(endDate).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithStartAndEndDate(final LocalDate startDate, final LocalDate endDate) {
        return ArcUpdateDtoBuilder.builder().startDate(startDate).endDate(endDate).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithTotalEstimatedMinutes(final int totalEstimatedMinutes) {
        return ArcUpdateDtoBuilder.builder().totalEstimatedMinutes(totalEstimatedMinutes).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithName(final String name) {
        return ArcUpdateDtoBuilder.builder().name(name).build().build();
    }

    public static ArcUpdateDto anArcUpdateDtoWithAllUpdatedFields(final int totalEstimatedMinutes, final String name, final LocalDate startDate, final LocalDate endDate) {
        return ArcUpdateDtoBuilder.builder().totalEstimatedMinutes(totalEstimatedMinutes).name(name).startDate(startDate).endDate(endDate).build().build();
    }

    public static ArcSummaryResponseDto anArcSummaryResponseDtoWithTasks() {
        return ArcSummaryResponseBuilder.builder().build().build();
    }
}