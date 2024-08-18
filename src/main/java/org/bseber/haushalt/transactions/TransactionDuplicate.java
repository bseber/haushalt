package org.bseber.haushalt.transactions;

import java.util.List;

public record TransactionDuplicate(NewTransaction candidate, List<Transaction> suggestions) {

    public boolean matches(HasTransactionFields transaction) {
        return transaction.bookingDate().equals(candidate.bookingDate())
            && transaction.amount().equals(candidate.amount());
    }
}
