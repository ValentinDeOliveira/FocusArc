package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

class UserIdConverterTest extends BaseIdConverterTest<UserId> {
    @Override
    protected Converter<String, UserId> converter() {
        return new UserIdConverter();
    }

    @Override
    protected UUID extractUuid(final UserId arcId) {
        return arcId.id();
    }

    @Override
    protected String invalidMessage(final String source) {
        return "Invalid UserId: " + source;
    }
}