package org.bseber.haushalt.transactions;

import jakarta.annotation.Nullable;
import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;


public final class Transaction extends NewTransaction {

    private final TransactionId id;
    private final String mappedPayee;

    public Transaction(TransactionId id, LocalDate bookingDate, @Nullable LocalDate valueDate, Procedure procedure,
                       @Nullable IBAN payerIban, String payer, @Nullable IBAN payeeIban, String payee, String mappedPayee,
                       RevenueType revenueType, Money amount, String reference, String customerReference, Status status) {
        super(bookingDate, valueDate, procedure, payerIban, payer, payeeIban, payee, revenueType, amount, reference, customerReference, status);
        this.id = id;
        this.mappedPayee = mappedPayee;
    }

    public TransactionId id() {
        return id;
    }

    public String mappedPayee() {
        return mappedPayee;
    }

    /**
     * returns {@link Transaction#mappedPayee()} when not empty, otherwise {@link Transaction#payee()}
     *
     * @return {@link Transaction#mappedPayee()} when not empty, otherwise {@link Transaction#payee()}
     */
    public String mappedPayeeOrPayee() {
        if (StringUtils.hasText(mappedPayee)) {
            return mappedPayee;
        }
        return super.payee();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + id +
            '}';
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
