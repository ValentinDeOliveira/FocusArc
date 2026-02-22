package com.valentin_d.focusarc.model.id;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record ChapterId(UUID id) {
    public static ChapterId random() {
        return new ChapterId(UUID.randomUUID());
    }

    @JsonValue
    public String asString() {
        return id.toString();
    }
}