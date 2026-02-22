package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.ChapterId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChapterIdConverter implements Converter<String, ChapterId> {

    @Override
    public ChapterId convert(final String source) {
        try {
            final var uuid = UUID.fromString(source);
            return new ChapterId(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ChapterId: " + source);
        }
    }
}