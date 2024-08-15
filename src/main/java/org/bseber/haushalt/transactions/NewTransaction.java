package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A new transaction to create.
 *
 * @param bookingDate booking date
 * @param valueDate optional value date, empty when status is {@link Transaction.Status#BOOKED booked}.
 * @param procedure procedure
 * @param payerIban optional payer {@link IBAN}
 * @param payer name of the payer
 * @param ibanPayee payee {@link IBAN}
 * @param payee name of the payee
 * @param revenueType revenue type
 * @param amount amount
 * @param reference reference
 * @param customerReference customer reference
 * @param status status
 */
public record NewTransaction(
    LocalDate bookingDate,
    Optional<LocalDate> valueDate,
    Transaction.Procedure procedure,
    Optional<IBAN> payerIban,
    String payer,
    IBAN ibanPayee,
    String payee,
    Transaction.RevenueType revenueType,
    Money amount,
    String reference,
    String customerReference,
    Transaction.Status status
) implements HasTransactionFields {
}
