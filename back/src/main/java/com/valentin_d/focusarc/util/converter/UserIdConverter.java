package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserIdConverter implements Converter<String, UserId> {

    @Override
    public UserId convert(final String source) {
        try {
            final UUID uuid = UUID.fromString(source);
            return new UserId(uuid);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid UserId: " + source);
        }
    }
}