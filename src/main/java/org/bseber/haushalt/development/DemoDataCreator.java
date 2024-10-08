package org.bseber.haushalt.development;

import org.bseber.haushalt.tags.TagService;
import org.bseber.haushalt.transactions.NewTransaction;
import org.bseber.haushalt.transactions.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.lang.invoke.MethodHandles.lookup;

class DemoDataCreator {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    private final DemoDataTransactionProvider demoDataTransactionProvider;
    private final DemoDataTagProvider demoDataTagProvider;
    private final TransactionService transactionService;
    private final TagService tagService;

    private static final Random RANDOM = new Random();

    DemoDataCreator(DemoDataTransactionProvider demoDataTransactionProvider, DemoDataTagProvider demoDataTagProvider,
                    TransactionService transactionService, TagService tagService) {
        this.demoDataTransactionProvider = demoDataTransactionProvider;
        this.demoDataTagProvider = demoDataTagProvider;
        this.transactionService = transactionService;
        this.tagService = tagService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        final Month month = LocalDate.now().getMonth();

        if (transactionService.findTransactionsForMonth(month).isEmpty()) {
            LOG.info("Start creating demo data for transactions.");
            createTransactions(month, RANDOM.nextInt(10, 100));
            createTransactions(month.minus(1), RANDOM.nextInt(10, 100));
            createTransactions(month.minus(2), RANDOM.nextInt(10, 100));
            createTransactions(month.minus(3), RANDOM.nextInt(10, 100));
            LOG.info("Finished creating demo data for transactions.");
        }

        if (tagService.findAllTags().isEmpty()) {
            tagService.createNewTag(demoDataTagProvider.createDemoTags());
        }
    }

    private void createTransactions(Month month, int amount) {

        final List<NewTransaction> transactions = IntStream.range(0, amount)
            .mapToObj(i -> demoDataTransactionProvider.createRandromTransaction(month)).toList();

        transactionService.addTransactions(transactions);
    }
}
