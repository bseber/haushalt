package org.bseber.haushalt.core;

import static org.springframework.util.StringUtils.hasText;

public record SanitizedString(String value) {

    public SanitizedString(String value) {
        this.value = sanitize(value);
    }

    /**
     * Sanitizes the given string and removes
     *
     * <ul>
     *     <li>leading/trailing spaces</li>
     * </ul>
     *
     * @param string to sanitize
     * @return the sanitized string
     */
    public static String sanitize(String string) {
        if (hasText(string)) {
            // TODO remove brackets and stuff
            return string.strip();
        }

        return "";
    }
}
