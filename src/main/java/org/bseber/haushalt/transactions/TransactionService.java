package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.Comparator.comparing;
import static org.springframework.util.StringUtils.hasText;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Optional<Transaction> getTransaction(TransactionId id) {
        return repository.findProjectionById(id.value())
            .map(TransactionService::toTransaction);
    }

    public TransactionBucket findTransactionsForMonth(Month month) {

        final LocalDate date = LocalDate.now().withMonth(month.getValue());
        final LocalDate firstDayOfMonth = date.with(firstDayOfMonth());
        final LocalDate lastDayOfMonth = date.with(lastDayOfMonth());

        final List<Transaction> transactions = repository.findAllByBookingDateAfterAndBookingDateBefore(firstDayOfMonth.minusDays(1), lastDayOfMonth.plusDays(1))
            .stream()
            .map(TransactionService::toTransaction)
            .sorted(comparing(Transaction::bookingDate))
            .toList();

        return new TransactionBucket(transactions);
    }

    public void addTransactions(List<NewTransaction> transactions) {

        final List<TransactionEntity> entities = transactions.stream()
            .map(TransactionService::toEntity)
            .toList();

        repository.saveAll(entities);
    }

    private static Transaction toTransaction(TransactionEntityProjection projection) {
        final String ibanPayer = projection.getIbanPayer();
        final String ibanPayee = projection.getIbanPayee();
        return new Transaction(
            new TransactionId(projection.getId()),
            projection.getBookingDate(),
            Optional.ofNullable(projection.getValueDate()),
            projection.getProcedure(),
            Optional.ofNullable(hasText(ibanPayer) ? new IBAN(ibanPayer) : null),
            projection.getPayer(),
            Optional.ofNullable(hasText(ibanPayee) ? new IBAN(ibanPayee) : null),
            projection.getPayee(),
            projection.getMappedPayee(),
            projection.getRevenueType(),
            new Money(projection.getAmount(), Currency.getInstance(projection.getCurrency())),
            projection.getReference(),
            projection.getCustomerReference(),
            projection.getStatus()
        );
    }

    private static TransactionEntity toEntity(HasTransactionFields transaction) {
        final TransactionEntity entity = new TransactionEntity();
        entity.setBookingDate(transaction.bookingDate());
        entity.setValueDate(transaction.valueDate().orElse(null));
        entity.setProcedure(transaction.procedure());
        entity.setIbanPayer(transaction.payerIban().map(IBAN::value).orElse(""));
        entity.setPayer(transaction.payer());
        entity.setIbanPayee(transaction.payeeIban().map(IBAN::value).orElse(""));
        entity.setPayee(transaction.payee());
        entity.setAmount(transaction.amount().amount());
        entity.setCurrency(transaction.amount().currency().getCurrencyCode());
        entity.setRevenueType(transaction.revenueType());
        entity.setReference(transaction.reference());
        entity.setCustomerReference(transaction.customerReference());
        entity.setStatus(transaction.status());
        return entity;
    }
}
