package org.bseber.haushalt.transactions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

class ImportPreviewDto {

    @Valid
    @NotNull
    private List<TransactionConflictDto> conflicts;

    @Valid
    @NotNull
    private List<TransactionImportDto> transactions;

    ImportPreviewDto() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    ImportPreviewDto(List<TransactionConflictDto> conflicts, List<TransactionImportDto> transactions) {
        this.conflicts = conflicts;
        this.transactions = transactions;
    }

    public List<TransactionConflictDto> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<TransactionConflictDto> conflicts) {
        this.conflicts = conflicts;
    }

    public List<TransactionImportDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionImportDto> transactions) {
        this.transactions = transactions;
    }
}
