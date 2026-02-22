package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.ArcId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ArcIdConverter implements Converter<String, ArcId> {

    @Override
    public ArcId convert(final String source) {
        try {
            final var uuid = UUID.fromString(source);
            return new ArcId(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ArcId: " + source);
        }
    }
}