package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.ChapterId;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

class ChapterIdConverterTest extends BaseIdConverterTest<ChapterId> {

    @Override
    protected Converter<String, ChapterId> converter() {
        return new ChapterIdConverter();
    }

    @Override
    protected UUID extractUuid(final ChapterId chapterId) {
        return chapterId.id();
    }

    @Override
    protected String invalidMessage(final String source) {
        return "Invalid ChapterId: " + source;
    }
}