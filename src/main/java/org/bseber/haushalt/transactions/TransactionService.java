package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

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

    public List<TransactionDuplicate> findTransactionDuplicates(List<NewTransaction> candidates) {

        final List<Example<TransactionEntity>> query = candidates.stream()
            .map(t -> {
                final TransactionEntity entity = new TransactionEntity();
                entity.setBookingDate(t.bookingDate());
                entity.setAmount(t.amount().amount());
                return Example.of(entity);
            })
            .toList();

        final List<Transaction> suggestions = repository.findAllBy(query).stream()
            .map(TransactionService::toTransaction)
            .toList();

        return candidates.stream()
            .map(candidate -> {
                final List<Transaction> candidateSuggestions = suggestions.stream()
                    .filter(s -> s.bookingDate().equals(candidate.bookingDate()) && s.amount().equals(candidate.amount()))
                    .toList();
                if (candidateSuggestions.isEmpty()) {
                    return null;
                } else {
                    return new TransactionDuplicate(candidate, candidateSuggestions);
                }
            })
            .filter(Objects::nonNull)
            .toList();
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

    @Transactional
    public TransactionCreationResult addTransactions(List<NewTransaction> newTransactions) {

        if (newTransactions.isEmpty()) {
            return new TransactionCreationResult(List.of(), List.of());
        }

        final List<TransactionDuplicate> duplicateCandidates = findTransactionDuplicates(newTransactions);

        final List<TransactionEntity> toSave = newTransactions.stream()
            .filter(t -> duplicateCandidates.stream().noneMatch(d -> d.candidate().equals(t)))
            .map(TransactionService::toEntity)
            .toList();

        final Iterable<TransactionEntity> saved = repository.saveAll(toSave);

        final List<Long> ids = StreamSupport.stream(saved.spliterator(), false).map(TransactionEntity::getId).toList();
        final List<Transaction> createdTransactions = repository.findAllProjectionsByIdIsIn(ids).stream()
            .map(TransactionService::toTransaction)
            .toList();

        return new TransactionCreationResult(createdTransactions, duplicateCandidates);
    }

    private static Transaction toTransaction(TransactionEntityProjection projection) {
        final String ibanPayer = projection.getIbanPayer();
        final String ibanPayee = projection.getIbanPayee();
        return new Transaction(
            new TransactionId(projection.getId()),
            projection.getBookingDate(),
            projection.getValueDate(),
            projection.getProcedure(),
            hasText(ibanPayer) ? new IBAN(ibanPayer) : null,
            projection.getPayer(),
            hasText(ibanPayee) ? new IBAN(ibanPayee) : null,
            projection.getPayee(),
            projection.getMappedPayee(),
            projection.getRevenueType(),
            new Money(projection.getAmount(), Currency.getInstance(projection.getCurrency())),
            projection.getReference(),
            projection.getCustomerReference(),
            projection.getStatus()
        );
    }

    private static Transaction toTransaction(TransactionEntity entity) {
        final String ibanPayer = entity.getIbanPayer();
        final String ibanPayee = entity.getIbanPayee();
        return new Transaction(
            new TransactionId(entity.getId()),
            entity.getBookingDate(),
            entity.getValueDate(),
            entity.getProcedure(),
            hasText(ibanPayer) ? new IBAN(ibanPayer) : null,
            entity.getPayer(),
            hasText(ibanPayee) ? new IBAN(ibanPayee) : null,
            entity.getPayee(),
            "", // TODO maybe?
            entity.getRevenueType(),
            new Money(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
            entity.getReference(),
            entity.getCustomerReference(),
            entity.getStatus()
        );
    }

    private static TransactionEntity toEntity(NewTransaction newTransaction) {
        final TransactionEntity entity = new TransactionEntity();
        entity.setBookingDate(newTransaction.bookingDate());
        entity.setValueDate(newTransaction.valueDate().orElse(null));
        entity.setProcedure(newTransaction.procedure());
        entity.setIbanPayer(newTransaction.payerIban().map(IBAN::value).orElse(""));
        entity.setPayer(newTransaction.payer());
        entity.setIbanPayee(newTransaction.payeeIban().map(IBAN::value).orElse(""));
        entity.setPayee(newTransaction.payee());
        entity.setAmount(newTransaction.amount().amount());
        entity.setCurrency(newTransaction.amount().currency().getCurrencyCode());
        entity.setRevenueType(newTransaction.revenueType());
        entity.setReference(newTransaction.reference());
        entity.setCustomerReference(newTransaction.customerReference());
        entity.setStatus(newTransaction.status());
        return entity;
    }
}
