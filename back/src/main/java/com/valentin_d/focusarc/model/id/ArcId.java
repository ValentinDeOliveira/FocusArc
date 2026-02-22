package com.valentin_d.focusarc.model.id;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record ArcId(UUID id) {
    public static ArcId random() {
        return new ArcId(UUID.randomUUID());
    }

    @JsonValue
    public String asString() {
        return id.toString();
    }
}