package com.valentin_d.focusarc.model.id;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record TagId(UUID id) {
    public static TagId random() {
        return new TagId(UUID.randomUUID());
    }

    @JsonValue
    public String asString() {
        return id.toString();
    }
}