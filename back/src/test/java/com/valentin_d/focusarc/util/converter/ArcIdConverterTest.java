package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.ArcId;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

class ArcIdConverterTest extends BaseIdConverterTest<ArcId> {

    @Override
    protected Converter<String, ArcId> converter() {
        return new ArcIdConverter();
    }

    @Override
    protected UUID extractUuid(final ArcId arcId) {
        return arcId.id();
    }

    @Override
    protected String invalidMessage(final String source) {
        return "Invalid ArcId: " + source;
    }
}