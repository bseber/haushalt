package org.bseber.haushalt.transactions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.Objects.requireNonNullElse;
import static org.bseber.haushalt.transactions.TransactionController.toTransactionListElementDto;

@Controller
@RequestMapping("/transactions/{id}")
class TransactionDetailController {

    private final TransactionService transactionService;

    TransactionDetailController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public String transaction(@PathVariable Long id, TransactionsFilterDto filter, Model model) {

        final LocalDate date = requireNonNullElse(filter.from(), LocalDate.now());
        final LocalDate from = requireNonNullElse(filter.from(), date.with(firstDayOfMonth()));
        final LocalDate to = filterToDate(filter);

        final TransactionBucket bucket = transactionService.findTransactionsForMonth(from.getMonth());

        model.addAttribute("transactions", transactionDtos(bucket, id));
        model.addAttribute("filter", new TransactionsFilterDto(from, to));

        final Optional<Transaction> transaction = transactionService.getTransaction(new TransactionId(id));
        if (transaction.isPresent()) {
            model.addAttribute("transaction", toViewDto(transaction.get()));
        } else {
            model.addAttribute("transaction", null);
        }

        return "transactions/detail";
    }

    private LocalDate filterToDate(TransactionsFilterDto filter) {
        if (filter.to() != null) {
            return filter.to();
        }

        final LocalDate from = filter.from();
        final LocalDate today = LocalDate.now();

        if (filter.from() == null || from.getMonth().equals(today.getMonth())) {
            return today;
        } else {
            return from.with(lastDayOfMonth());
        }
    }

    private List<TransactionListElementDto> transactionDtos(TransactionBucket bucket, Long activeTransactionId) {
        return bucket.transactions()
            .stream()
            .map(t -> toTransactionListElementDto(t, activeTransactionId.equals(t.id().value())))
            .toList();
    }

    static TransactionDetailDto toViewDto(Transaction transaction) {
        return new TransactionDetailDto(
            transaction.bookingDate(),
            transaction.valueDate().orElse(null),
            transaction.procedure(),
            transaction.payer(),
            transaction.payee(),
            transaction.mappedPayee(),
            transaction.reference(),
            transaction.revenueType(),
            transaction.ibanPayee().value(),
            transaction.amount().amount(),
            transaction.customerReference(),
            transaction.status()
        );
    }
}
