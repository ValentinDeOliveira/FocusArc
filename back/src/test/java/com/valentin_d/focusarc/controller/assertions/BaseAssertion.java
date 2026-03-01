package com.valentin_d.focusarc.controller.assertions;

import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.valentin_d.focusarc.helpers.TestConstants.DATE_TIME_FORMATTER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public abstract class BaseAssertion<T> {
    public void assertSingleJson(final ResultActions actions, final T expected) throws Exception {
        assertJson(actions, "$", expected);
    }

    public void assertListJson(final ResultActions actions, final T expected) throws Exception {
        assertJson(actions, "$[0]", expected);
    }

    public void assertListPathJson(final ResultActions actions, final String path, final List<T> expected) throws Exception {
        for (int i = 0; i < expected.size(); i++) {
            assertPathJson(actions, "." + path + "[" + i + "]", expected.get(i));
        }
    }

    public void assertPathJson(final ResultActions actions, final String path, final T expected) throws Exception {
        assertJson(actions, path, expected);
    }

    protected abstract void assertJson(final ResultActions actions, final String path, final T expected) throws Exception;

    protected void expect(final ResultActions actions, final String path, final String field, final Object value) throws Exception {
        actions.andExpect(jsonPath(path + "." + field).value(value));
    }

    protected String formatDateTime(final LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}