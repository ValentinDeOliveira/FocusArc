package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.TagId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TagIdConverter implements Converter<String, TagId> {

    @Override
    public TagId convert(final String source) {
        try {
            return new TagId(UUID.fromString(source));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TagId: " + source);
        }
    }
}
