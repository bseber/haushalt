package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Marker interface for {@link Transaction} and {@link NewTransaction} to ensure same fields.
 */
interface HasTransactionFields {

    LocalDate bookingDate();

    Optional<LocalDate> valueDate();

    Transaction.Procedure procedure();

    Optional<IBAN> payerIban();

    String payer();

    IBAN ibanPayee();

    String payee();

    Transaction.RevenueType revenueType();

    Money amount();

    String reference();

    String customerReference();

    Transaction.Status status();

}
