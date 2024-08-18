package org.bseber.haushalt.transactions;

import java.util.List;

public record TransactionCreationResult(List<Transaction> newTransactions, List<TransactionDuplicate> duplicates) {

    public boolean isSuccess() {
        return duplicates.isEmpty();
    }
}
