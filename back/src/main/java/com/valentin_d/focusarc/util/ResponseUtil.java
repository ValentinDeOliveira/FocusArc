package com.valentin_d.focusarc.util;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class ResponseUtil {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> ResponseEntity<T> wrapOrNotFound(final Optional<T> maybeEntity) {
        return maybeEntity
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static <T> ResponseEntity<List<T>> wrapOrNoContent(final List<T> maybeEntity) {
        return maybeEntity.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(maybeEntity);
    }
}