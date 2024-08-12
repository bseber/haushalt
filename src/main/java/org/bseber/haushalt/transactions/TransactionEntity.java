package org.bseber.haushalt.transactions;

import jakarta.annotation.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Table("transaction")
class TransactionEntity {

    @Id
    private Long id;

    private LocalDate bookingDate;

    private LocalDate valueDate;

    private Transaction.Procedure procedure;

    @Nullable
    private String ibanPayer;

    private String payer;

    private String ibanPayee;

    private String payee;

    private Transaction.RevenueType revenueType;

    private BigDecimal amount;

    private String currency;

    private String reference;

    @Nullable
    private String customerReference;

    private Transaction.Status status;

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public Transaction.Procedure getProcedure() {
        return procedure;
    }

    void setProcedure(Transaction.Procedure procedure) {
        this.procedure = procedure;
    }

    @Nullable
    public String getIbanPayer() {
        return ibanPayer;
    }

    void setIbanPayer(@Nullable String ibanPayer) {
        this.ibanPayer = ibanPayer;
    }

    public String getPayer() {
        return payer;
    }

    void setPayer(String payer) {
        this.payer = payer;
    }

    public String getIbanPayee() {
        return ibanPayee;
    }

    void setIbanPayee(String ibanPayee) {
        this.ibanPayee = ibanPayee;
    }

    public String getPayee() {
        return payee;
    }

    void setPayee(String payee) {
        this.payee = payee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }

    public Transaction.RevenueType getRevenueType() {
        return revenueType;
    }

    void setRevenueType(Transaction.RevenueType revenueType) {
        this.revenueType = revenueType;
    }

    public String getReference() {
        return reference;
    }

    void setReference(String reference) {
        this.reference = reference;
    }

    @Nullable
    public String getCustomerReference() {
        return customerReference;
    }

    void setCustomerReference(@Nullable String customerReference) {
        this.customerReference = customerReference;
    }

    public Transaction.Status getStatus() {
        return status;
    }

    void setStatus(Transaction.Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
