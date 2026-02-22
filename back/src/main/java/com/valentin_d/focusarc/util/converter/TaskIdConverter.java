package com.valentin_d.focusarc.util.converter;

import com.valentin_d.focusarc.model.id.TaskId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskIdConverter implements Converter<String, TaskId> {

    @Override
    public TaskId convert(final String source) {
        try {
            final var uuid = UUID.fromString(source);
            return new TaskId(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TaskId: " + source);
        }
    }
}