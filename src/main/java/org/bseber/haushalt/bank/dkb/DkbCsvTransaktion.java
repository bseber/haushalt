package org.bseber.haushalt.bank.dkb;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;

import java.time.LocalDate;
import java.util.Optional;

public record DkbCsvTransaktion(
    LocalDate bookingDate,
    Optional<LocalDate> valueDate,
    Status status,
    String payer,
    String payee,
    String reference,
    Type type,
    IBAN iban,
    Money amount,
    String mandateReference,
    String customerReference
) {

    public enum Status {
        GEBUCHT,
        VORGEMERKT,
    }

    public enum Type {
        EINGANG,
        AUSGANG,
    }
}
