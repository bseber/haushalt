package org.bseber.haushalt.core;

import org.springframework.util.Assert;

public record IBAN(String value) {

    public IBAN {
        Assert.hasText(value, "IBAN value must not be empty");
    }
}
