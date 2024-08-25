package org.bseber.haushalt.tags;

import org.bseber.haushalt.core.SanitizedString;

/**
 * Represents the description of a {@link Tag}.
 *
 * @param value description of the tag, never {@code null}
 */
public record TagDescription(String value) {

    public TagDescription(String value) {
        this.value = value == null ? "" : value.strip();
    }

    public static TagDescription of(SanitizedString sanitized) {
        return new TagDescription(sanitized.value());
    }

    public static TagDescription sanitized(String userInput) {
        return of(new SanitizedString(userInput));
    }

    public static TagDescription empty() {
        return new TagDescription("");
    }
}
