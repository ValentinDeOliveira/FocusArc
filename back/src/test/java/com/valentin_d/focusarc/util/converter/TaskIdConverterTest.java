package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.TaskId;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

class TaskIdConverterTest extends BaseIdConverterTest<TaskId> {
    @Override
    protected Converter<String, TaskId> converter() {
        return new TaskIdConverter();
    }

    @Override
    protected UUID extractUuid(final TaskId arcId) {
        return arcId.id();
    }

    @Override
    protected String invalidMessage(final String source) {
        return "Invalid TaskId: " + source;
    }
}