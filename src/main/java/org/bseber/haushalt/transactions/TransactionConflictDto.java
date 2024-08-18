package org.bseber.haushalt.transactions;

import java.util.List;

@TransactionConflictConstraintWhenImported
class TransactionConflictDto {

    private boolean importMe;
    private TransactionImportDto transaction;
    private List<TransactionImportDto> suggestions;

    TransactionConflictDto() {
    }

    TransactionConflictDto(TransactionImportDto transaction, List<TransactionImportDto> suggestions) {
        this.transaction = transaction;
        this.suggestions = suggestions;
    }

    public boolean isImportMe() {
        return importMe;
    }

    public void setImportMe(boolean importMe) {
        this.importMe = importMe;
    }

    public TransactionImportDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionImportDto transaction) {
        this.transaction = transaction;
    }

    public List<TransactionImportDto> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<TransactionImportDto> suggestions) {
        this.suggestions = suggestions;
    }
}
