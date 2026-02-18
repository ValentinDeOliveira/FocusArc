package com.valentin_d.focusarc.model.id;

import java.util.UUID;

public record UserId(UUID id) {
    public static UserId random() {
        return new UserId(UUID.randomUUID());
    }
}
