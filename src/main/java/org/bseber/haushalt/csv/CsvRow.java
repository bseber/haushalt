package org.bseber.haushalt.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Objects.requireNonNullElse;
import static java.util.function.Function.identity;

public record CsvRow(List<String> columns) {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    /**
     *
     * @param index column index
     * @param defaultValue default value to use then index is out of range or mapped value is {@code null}
     * @return the mapped value, or defaultValue when {@code null} or index is out of range
     */
    public String valueOrElse(int index, String defaultValue) {
        return valueOrElse(index, identity(), defaultValue);
    }

    /**
     *
     * @param index column index
     * @param mapper map column string value into T
     * @return the mapped value, never {@code null}
     * @param <T> type of final value
     * @throws IllegalStateException when mapped value is {@code null} or index is out of range.
     */
    public <T> T value(int index, Function<String, T> mapper) {
        T value = valueOrElse(index, mapper, null);
        if (value == null) {
            throw new IllegalStateException("could not extract value with index %s from row.".formatted(index));
        }
        return value;
    }

    /**
     *
     * @param index column index
     * @param mapper map column string value to T
     * @param defaultValue default value to use when index is out of range or mapped value is {@code null}
     * @return the mapped value or defaultValue, never {@code null} (only when defaultValue is {@code null})
     * @param <T> type of the final value
     */
    public <T> T valueOrElse(int index, Function<String, T> mapper, T defaultValue) {
        T value = null;
        try {
            value = mapper.apply(columns.get(index));
        } catch(RuntimeException e) {
            LOG.debug("could not extract index={} fom row={}", index, columns, e);
        }
        if (defaultValue == null) {
            return value;
        }
        return requireNonNullElse(value, defaultValue);
    }
}
