package org.bseber.haushalt.transactions;

import jakarta.annotation.Nullable;
import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * A new {@link Transaction} to create.
 */
public sealed class NewTransaction permits Transaction {

    private final LocalDate bookingDate;
    @Nullable
    private final LocalDate valueDate;
    private final Transaction.Procedure procedure;
    @Nullable
    private final IBAN payerIban;
    private final String payer;
    @Nullable
    private final IBAN payeeIban;
    private final String payee;
    private final Transaction.RevenueType revenueType;
    private final Money amount;
    private final String reference;
    private final String customerReference;
    private final Transaction.Status status;

    public NewTransaction(LocalDate bookingDate, @Nullable LocalDate valueDate, Transaction.Procedure procedure,
                          @Nullable IBAN payerIban, String payer, @Nullable IBAN payeeIban, String payee,
                          Transaction.RevenueType revenueType, Money amount, String reference, String customerReference,
                          Transaction.Status status) {
        this.bookingDate = bookingDate;
        this.valueDate = valueDate;
        this.procedure = procedure;
        this.payerIban = payerIban;
        this.payer = payer;
        this.payeeIban = payeeIban;
        this.payee = payee;
        this.revenueType = revenueType;
        this.amount = amount;
        this.reference = reference;
        this.customerReference = customerReference;
        this.status = status;
    }

    public LocalDate bookingDate() {
        return bookingDate;
    }

    public Optional<LocalDate> valueDate() {
        return Optional.ofNullable(valueDate);
    }

    public Transaction.Procedure procedure() {
        return procedure;
    }

    public Optional<IBAN> payerIban() {
        return Optional.ofNullable(payerIban);
    }

    public String payer() {
        return payer;
    }

    public Optional<IBAN> payeeIban() {
        return Optional.ofNullable(payeeIban);
    }

    public String payee() {
        return payee;
    }

    public Transaction.RevenueType revenueType() {
        return revenueType;
    }

    public Money amount() {
        return amount;
    }

    public String reference() {
        return reference;
    }

    public String customerReference() {
        return customerReference;
    }

    public Transaction.Status status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewTransaction that = (NewTransaction) o;
        return Objects.equals(bookingDate, that.bookingDate)
            && Objects.equals(valueDate, that.valueDate)
            && procedure == that.procedure
            && Objects.equals(payerIban, that.payerIban)
            && Objects.equals(payer, that.payer)
            && Objects.equals(payeeIban, that.payeeIban)
            && Objects.equals(payee, that.payee)
            && revenueType == that.revenueType
            && Objects.equals(amount, that.amount)
            && Objects.equals(reference, that.reference)
            && Objects.equals(customerReference, that.customerReference)
            && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingDate, valueDate, procedure, payerIban, payer, payeeIban, payee, revenueType, amount, reference, customerReference, status);
    }

    @Override
    public String toString() {
        return "NewTransaction{" +
            "bookingDate=" + bookingDate +
            ", valueDate=" + valueDate +
            ", procedure=" + procedure +
            ", payerIban=" + payerIban +
            ", payer='" + payer + '\'' +
            ", payeeIban=" + payeeIban +
            ", payee='" + payee + '\'' +
            ", revenueType=" + revenueType +
            ", amount=" + amount +
            ", reference='" + reference + '\'' +
            ", customerReference='" + customerReference + '\'' +
            ", status=" + status +
            '}';
    }
}
