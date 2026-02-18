package com.valentin_d.focusarc.util;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ResponseUtil {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> ResponseEntity<T> wrapOrNotFound(final Optional<T> maybeEntity) {
        return maybeEntity
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
