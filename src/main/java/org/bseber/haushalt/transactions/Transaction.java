package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @param id id of the transaction
 * @param bookingDate booking date
 * @param valueDate optional value date, empty when status is {@link Transaction.Status#BOOKED booked}.
 * @param procedure procedure
 * @param payerIban optional payer {@link IBAN}
 * @param payer name of the payer
 * @param payeeIban optional payee {@link IBAN}
 * @param payee name of the payee
 * @param mappedPayee user mapped name of the payee. (e.g. "Amazon Payments Europe XYZ" is mapped to "Amazon")
 * @param revenueType revenue type
 * @param amount amount
 * @param reference reference
 * @param customerReference customer reference
 * @param status status
 */
public record Transaction(
    TransactionId id,
    LocalDate bookingDate,
    Optional<LocalDate> valueDate,
    Procedure procedure,
    Optional<IBAN> payerIban,
    String payer,
    Optional<IBAN> payeeIban,
    String payee,
    String mappedPayee,
    RevenueType revenueType,
    Money amount,
    String reference,
    String customerReference,
    Transaction.Status status
) implements HasTransactionFields {

    /**
     * returns {@link Transaction#mappedPayee()} when not empty, otherwise {@link Transaction#payee()}
     *
     * @return {@link Transaction#mappedPayee()} when not empty, otherwise {@link Transaction#payee()}
     */
    public String mappedPayeeOrPayee() {
        if (StringUtils.hasText(mappedPayee)) {
            return mappedPayee;
        }
        return payee;
    }

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
