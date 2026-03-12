package com.valentin_d.focusarc.controller.assertions;

import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.valentin_d.focusarc.helpers.TestConstants.DATE_TIME_FORMATTER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public abstract class BaseAssertion<T> {
    public void assertSingleJson(final ResultActions actions, final T expected) throws Exception {
        assertJson(actions, "$", expected);
    }

    public void assertListJson(final ResultActions actions, final T expected) throws Exception {
        assertJson(actions, "$[0]", expected);
    }

    public void assertListPathJson(final ResultActions actions, final String path, final Collection<T> expected) throws Exception {
        int i = 0;
        for (final T element : expected) {
            assertPathJson(actions, "." + path + "[" + i++ + "]", element);
        }
    }

    public void assertPathJson(final ResultActions actions, final String path, final T expected) throws Exception {
        assertJson(actions, path, expected);
    }

    protected abstract void assertJson(final ResultActions actions, final String path, final T expected) throws Exception;

    protected void assertValues(final ResultActions actions, final String path, final Collection<?> values) throws Exception {
        int i = 0;
        for (final Object value : values) {
            actions.andExpect(jsonPath(path + "[" + i++ + "]").value(value.toString()));
        }
    }

    protected void expect(final ResultActions actions, final String path, final String field, final Object value) throws Exception {
        actions.andExpect(jsonPath(path + "." + field).value(value));
    }

    protected String formatDateTime(final LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}