package org.bseber.haushalt.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ImportPreviewDto {

    private List<TransactionDto> transactions;

    public ImportPreviewDto() {
        this.transactions = new ArrayList<>();
    }

    public ImportPreviewDto(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ImportPreviewDto) obj;
        return Objects.equals(this.transactions, that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactions);
    }

    @Override
    public String toString() {
        return "ImportPreviewDto[" +
            "transactions=" + transactions + ']';
    }

}
