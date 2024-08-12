package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;

import java.time.LocalDate;
import java.util.Optional;

public record Transaction(
    LocalDate bookingDate,
    Optional<LocalDate> valueDate,
    Procedure procedure,
    Optional<IBAN> payerIban,
    String payer,
    IBAN ibanPayee,
    String payee,
    RevenueType revenueType,
    Money amount,
    String reference,
    String customerReference,
    Transaction.Status status
) {

    public enum RevenueType {
        AMOUNT_COMING_IN,
        AMOUNT_COMING_OUT,
    }

    public enum Procedure {
        UNKNOWN,
        CASH_PAYMENT,
        DEBIT_CARD_PAYMENT,
        DEBIT_CARD_PAYMENT_ONLINE,
        TRANSFER,
        STANDING_ORDER,
        SALARY
    }

    public enum Status {
        BOOKED,
        EXPENSED,
    }
}
