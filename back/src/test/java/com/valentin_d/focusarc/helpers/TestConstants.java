package com.valentin_d.focusarc.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestConstants {
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final LocalDateTime NOW = LocalDateTime.parse(
            LocalDateTime.now().format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER
    );
    public static final LocalDate TODAY = NOW.toLocalDate();
}