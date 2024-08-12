package org.bseber.haushalt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class DateParser {

    protected DateParser() {
        //
    }

    public static LocalDate parseDate(String date, String pattern) {
        return parseDate(date, List.of(pattern));
    }

    public static LocalDate parseDate(String date, List<String> datePatterns) {
        for (String pattern : datePatterns) {
            final Optional<LocalDate> value = parse(date, DateTimeFormatter.ofPattern(pattern));
            if (value.isPresent()) {
                return value.get();
            }
        }
        throw new DateTimeParseException("Unable to parse date: " + date, date, 0);
    }

    private static Optional<LocalDate> parse(String date, DateTimeFormatter formatter) {
        try {
            return Optional.of(LocalDate.parse(date, formatter));
        } catch(DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
