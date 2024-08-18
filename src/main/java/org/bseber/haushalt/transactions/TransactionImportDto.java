package org.bseber.haushalt.transactions;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class TransactionImportDto {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate valueDate;

    @NotNull
    private Transaction.Procedure procedure;

    @NotEmpty
    private String payer;

    @NotEmpty
    private String payee;

    private String reference;

    @NotNull
    private Transaction.RevenueType revenueType;

    private String iban;

    @NotNull
    @NumberFormat(pattern = "#,###.00")
    private BigDecimal amount;

    private String customerReference;

    @NotNull
    private Transaction.Status status;

    public TransactionImportDto() {
    }

    public TransactionImportDto(LocalDate bookingDate, LocalDate valueDate, Transaction.Procedure procedure,
                                String payer, String payee, String reference, Transaction.RevenueType revenueType,
                                String iban, BigDecimal amount, String customerReference, Transaction.Status status) {
        this.bookingDate = bookingDate;
        this.valueDate = valueDate;
        this.procedure = procedure;
        this.payer = payer;
        this.payee = payee;
        this.reference = reference;
        this.revenueType = revenueType;
        this.iban = iban;
        this.amount = amount;
        this.customerReference = customerReference;
        this.status = status;
    }

    public @NotNull LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(@NotNull LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(@Nullable LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public @NotNull Transaction.Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(@NotNull Transaction.Procedure procedure) {
        this.procedure = procedure;
    }

    public @NotEmpty String getPayer() {
        return payer;
    }

    public void setPayer(@NotEmpty String payer) {
        this.payer = payer;
    }

    public @NotEmpty String getPayee() {
        return payee;
    }

    public void setPayee(@NotEmpty String payee) {
        this.payee = payee;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public @NotNull Transaction.RevenueType getRevenueType() {
        return revenueType;
    }

    public void setRevenueType(@NotNull Transaction.RevenueType revenueType) {
        this.revenueType = revenueType;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public @NotNull BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull BigDecimal amount) {
        this.amount = amount;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public @NotNull Transaction.Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull Transaction.Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TransactionImportDto) obj;
        return Objects.equals(this.bookingDate, that.bookingDate) &&
            Objects.equals(this.valueDate, that.valueDate) &&
            Objects.equals(this.procedure, that.procedure) &&
            Objects.equals(this.payer, that.payer) &&
            Objects.equals(this.payee, that.payee) &&
            Objects.equals(this.reference, that.reference) &&
            Objects.equals(this.revenueType, that.revenueType) &&
            Objects.equals(this.iban, that.iban) &&
            Objects.equals(this.amount, that.amount) &&
            Objects.equals(this.customerReference, that.customerReference) &&
            Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingDate, valueDate, procedure, payer, payee, reference, revenueType, iban, amount, customerReference, status);
    }

    @Override
    public String toString() {
        return "TransactionImportDto[" +
            "bookingDate=" + bookingDate + ", " +
            "valueDate=" + valueDate + ", " +
            "procedure=" + procedure + ", " +
            "payer=" + payer + ", " +
            "payee=" + payee + ", " +
            "reference=" + reference + ", " +
            "revenueType=" + revenueType + ", " +
            "iban=" + iban + ", " +
            "amount=" + amount + ", " +
            "customerReference=" + customerReference + ", " +
            "status=" + status + "]";
    }

}
