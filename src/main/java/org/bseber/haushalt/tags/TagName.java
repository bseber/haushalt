package org.bseber.haushalt.tags;

import org.bseber.haushalt.core.SanitizedString;
import org.springframework.util.StringUtils;

/**
 * Represents the description of a {@link Tag}.
 *
 * @param value value of the tag name, never {@code null} nor blank
 */
public record TagName(String value) {

    public TagName {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Tag name value must have text.");
        }
    }

    public static TagName of(SanitizedString sanitized) {
        return new TagName(sanitized.value());
    }

    public static TagName sanitized(String userInput) {
        return of(new SanitizedString(userInput));
    }
}
