package org.bseber.haushalt.transactions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public record TransactionBucket(List<Transaction> transactions) {

    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    public <T> Map<T, List<Transaction>> transactionBy(Function<Transaction, T> extractor) {
        return transactions.stream().collect(groupingBy(extractor, toList()));
    }
}
