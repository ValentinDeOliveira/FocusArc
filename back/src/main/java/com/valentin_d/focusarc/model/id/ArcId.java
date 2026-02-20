package com.valentin_d.focusarc.model.id;

import java.util.UUID;

public record ArcId(UUID id) {
    public static ArcId random() {
        return new ArcId(UUID.randomUUID());
    }
}