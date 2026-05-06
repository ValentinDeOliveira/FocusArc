package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.util.validation.ValidMinutes;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;

import static com.valentin_d.focusarc.shared.SizeConstraints.MID_MAX_LENGTH;

// TODO: Check estimated minutes < completed minutes + estimated minutes | completed minutes < Short.MAX_VALUE
@Builder
public record TaskUpdateDto(@ValidMinutes Integer completedMinutes,
                            @ValidMinutes Integer estimatedMinutes,
                            @FutureOrPresent Instant scheduledAt, TaskStatus taskStatus,
                            @Size(max = MID_MAX_LENGTH) String name,
                            String description, @Nullable TagId tagId) {
}