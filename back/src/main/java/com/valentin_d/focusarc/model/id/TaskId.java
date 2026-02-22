package com.valentin_d.focusarc.model.id;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record TaskId(UUID id) {
    public static TaskId random() {
        return new TaskId(UUID.randomUUID());
    }

    @JsonValue
    public String asString() {
        return id.toString();
    }
}