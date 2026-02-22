package com.valentin_d.focusarc.util.converter;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

abstract class BaseIdConverterTest<ID> {
    protected abstract Converter<String, ID> converter();
    protected abstract UUID extractUuid(final ID id);
    @SuppressWarnings("SameParameterValue")
    protected abstract String invalidMessage(final String source);

    @Test
    void shouldConvertValidUuidString() {
        final var uuid = UUID.randomUUID();

        final var id = converter().convert(uuid.toString());

        assertNotNull(id);
        assertEquals(uuid, extractUuid(id));
    }

    @Test
    void shouldThrowException_whenUuidIsInvalid() {
        final var invalid = "not-a-uuid";

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> converter().convert(invalid)
        );

        assertEquals(invalidMessage(invalid), ex.getMessage());
    }
}