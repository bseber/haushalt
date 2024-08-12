package org.bseber.haushalt.development;

import org.bseber.haushalt.transactions.Transaction;
import org.bseber.haushalt.transactions.TransactionService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

class DemoDataCreator {

    private final DemoDataTransactionProvider demoDataTransactionProvider;
    private final TransactionService transactionService;

    private static final Random RANDOM = new Random();

    DemoDataCreator(DemoDataTransactionProvider demoDataTransactionProvider, TransactionService transactionService) {
        this.demoDataTransactionProvider = demoDataTransactionProvider;
        this.transactionService = transactionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        final Month month = LocalDate.now().getMonth();

        if (transactionService.findTransactionsForMonth(month).isEmpty()) {
            createTransactions(month, RANDOM.nextInt(10, 100));
            createTransactions(month.minus(1), RANDOM.nextInt(10, 100));
            createTransactions(month.minus(2), RANDOM.nextInt(10, 100));
            createTransactions(month.minus(3), RANDOM.nextInt(10, 100));
        }
    }

    private void createTransactions(Month month, int amount) {

        final List<Transaction> transactions = IntStream.range(0, amount)
            .mapToObj(i -> demoDataTransactionProvider.createRandromTransaction(month)).toList();

        transactionService.addTransactions(transactions);
    }
}
