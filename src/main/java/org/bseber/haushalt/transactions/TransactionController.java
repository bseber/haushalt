package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.Money;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.Objects.requireNonNullElse;
import static org.bseber.haushalt.transactions.Transaction.RevenueType.AMOUNT_COMING_OUT;

@Controller
@RequestMapping("/transactions")
class TransactionController {

    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public String transactions(TransactionsFilterDto filter, Model model,
                               @RequestHeader(value = "x-turbo-request-id", required = false) Optional<UUID> turboRequest) {

        final LocalDate date = requireNonNullElse(filter.from(), LocalDate.now());
        final LocalDate from = requireNonNullElse(filter.from(), date.with(firstDayOfMonth()));
        final LocalDate to = filterToDate(filter);

        final TransactionBucket bucket = transactionService.findTransactionsForMonth(from.getMonth());

        model.addAttribute("transactions", transactionDtos(bucket));
        model.addAttribute("expensesChartData", expensesChartRows(bucket));
        model.addAttribute("filter", new TransactionsFilterDto(from, to));

//        if (turboRequest.isPresent()) {
//            return "transactions/index::#frame-transactions";
//        } else {
            return "transactions/index";
//        }
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

    private List<TransactionListElementDto> transactionDtos(TransactionBucket bucket) {
        return bucket.transactions()
            .stream()
            .map(transaction -> toTransactionListElementDto(transaction, false))
            .toList();
    }

    private static List<ExpensesChartRowDto> expensesChartRows(TransactionBucket bucket) {
        return bucket.transactionBy(Transaction::mappedPayeeOrPayee).entrySet().stream()
            .map(entry -> new ExpensesChartRowDto("April", entry.getKey(), summedExpenses(entry.getValue())))
            .filter(row -> !row.amount().equals(BigDecimal.ZERO))
            .toList();
    }

    private static BigDecimal summedExpenses(List<Transaction> transactions) {
        return transactions.stream()
            .filter(t -> AMOUNT_COMING_OUT == t.revenueType())
            .map(Transaction::amount)
            .map(Money::amount)
            .map(BigDecimal::negate) // expenses are calculated/shown -> show a positive number
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static TransactionListElementDto toTransactionListElementDto(Transaction transaction, boolean active) {
        return new TransactionListElementDto(
            transaction.id().value(),
            transaction.mappedPayeeOrPayee(),
            transaction.reference(),
            transaction.amount().amount(),
            transaction.bookingDate(),
            active
        );
    }
}
