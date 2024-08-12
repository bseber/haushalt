package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.springframework.util.StringUtils.hasText;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public TransactionBucket findTransactionsForMonth(Month month) {

        final LocalDate date = LocalDate.now().withMonth(month.getValue());
        final LocalDate firstDayOfMonth = date.with(firstDayOfMonth());
        final LocalDate lastDayOfMonth = date.with(lastDayOfMonth());

        final List<Transaction> transactions = repository.findAllByBookingDateAfterAndBookingDateBefore(firstDayOfMonth.minusDays(1), lastDayOfMonth.plusDays(1))
            .stream()
            .map(TransactionService::toTransaction)
            .toList();

        return new TransactionBucket(transactions);
    }

    public List<Transaction> addTransactions(List<Transaction> transactions) {

        final List<TransactionEntity> entities = transactions.stream()
            .map(TransactionService::toEntity)
            .toList();

        return toList(repository.saveAll(entities), TransactionService::toTransaction);
    }

    private static Transaction toTransaction(TransactionEntity entity) {
        final String ibanAuftraggeber = entity.getIbanPayer();
        return new Transaction(
            entity.getBookingDate(),
            Optional.ofNullable(entity.getValueDate()),
            entity.getProcedure(),
            Optional.ofNullable(hasText(ibanAuftraggeber) ? new IBAN(ibanAuftraggeber) : null),
            entity.getPayer(),
            new IBAN(entity.getIbanPayee()),
            entity.getPayee(),
            entity.getRevenueType(),
            new Money(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
            entity.getReference(),
            entity.getCustomerReference(),
            entity.getStatus()
        );
    }

    private static TransactionEntity toEntity(Transaction transaction) {
        final TransactionEntity entity = new TransactionEntity();
        entity.setBookingDate(transaction.bookingDate());
        entity.setValueDate(transaction.valueDate().orElse(null));
        entity.setProcedure(transaction.procedure());
        entity.setIbanPayer(transaction.payerIban().map(IBAN::value).orElse(""));
        entity.setPayer(transaction.payer());
        entity.setIbanPayee(transaction.ibanPayee().value());
        entity.setPayee(transaction.payee());
        entity.setAmount(transaction.amount().amount());
        entity.setCurrency(transaction.amount().currency().getCurrencyCode());
        entity.setRevenueType(transaction.revenueType());
        entity.setReference(transaction.reference());
        entity.setCustomerReference(transaction.customerReference());
        entity.setStatus(transaction.status());
        return entity;
    }

    private static <T, R> List<R> toList(Iterable<T> iterable, Function<T, R> mapper) {
        return StreamSupport.stream(iterable.spliterator(), false).map(mapper).toList();
    }
}
