package org.bseber.haushalt.transactions;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.tags.Tag;
import org.bseber.haushalt.tags.TagName;
import org.bseber.haushalt.tags.TagService;
import org.bseber.haushalt.web.exceptions.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.Objects.requireNonNullElse;
import static java.util.function.Predicate.not;
import static org.bseber.haushalt.transactions.TransactionController.toTransactionListElementDto;
import static org.springframework.util.StringUtils.hasText;

@Controller
@RequestMapping("/transactions/{id}")
class TransactionDetailController {

    private final TransactionService transactionService;
    private final TagService tagService;

    TransactionDetailController(TransactionService transactionService, TagService tagService) {
        this.transactionService = transactionService;
        this.tagService = tagService;
    }

    @GetMapping
    public String transaction(@PathVariable Long id, TransactionsFilterDto filter, Model model) {

        final Optional<Transaction> maybeTransaction = transactionService.getTransaction(new TransactionId(id));
        if (maybeTransaction.isEmpty()) {
            throw new NotFoundException("Could not find transaction with id " + id);
        }

        final LocalDate date = requireNonNullElse(filter.from(), LocalDate.now());
        final LocalDate from = requireNonNullElse(filter.from(), date.with(firstDayOfMonth()));
        final LocalDate to = filterToDate(filter);
        model.addAttribute("filter", new TransactionsFilterDto(from, to));

        final TransactionBucket bucket = transactionService.findTransactionsForMonth(from.getMonth());
        model.addAttribute("transactions", transactionDtos(bucket, id));

        final Transaction transaction = maybeTransaction.get();
        final List<Tag> transactionTags = tagService.findAllTagsForTransaction(transaction.id());
        model.addAttribute("transaction", toTransactionDetailDto(transaction, transactionTags));

        final List<Tag> notYetAssignedTags = tagService.findAllTags().stream().filter(not(transactionTags::contains)).toList();
        model.addAttribute("allTagNames", notYetAssignedTags.stream().map(Tag::name).map(TagName::value).toList());

        return "transactions/detail";
    }

    @PostMapping("/tags")
    public String updateTransactionTags(@PathVariable Long id, @RequestParam(value = "tag", required = false, defaultValue = "") List<String> tags) {

        final List<TagName> tagNames = tags.stream().map(TagName::sanitized).toList();

        tagService.updateTagsOfTransaction(new TransactionId(id), tagNames);
        return "redirect:/transactions/" + id;
    }

    @PostMapping(value = "/tags/add")
    public String addTagToTransaction(@PathVariable Long id, @RequestParam("tag") String tag) {

        if (!hasText(tag)) {
            // TODO error feedback
            return "redirect:/transactions/" + id;
        }

        tagService.addTagToTransaction(new TransactionId(id), TagName.sanitized(tag));
        return "redirect:/transactions/" + id;
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

    static TransactionDetailDto toTransactionDetailDto(Transaction transaction, List<Tag> tags) {
        return new TransactionDetailDto(
            transaction.id().value(),
            transaction.bookingDate(),
            transaction.valueDate().orElse(null),
            transaction.procedure(),
            transaction.payer(),
            transaction.payee(),
            transaction.mappedPayee(),
            transaction.reference(),
            transaction.revenueType(),
            transaction.payeeIban().map(IBAN::value).orElse(""),
            transaction.amount().amount(),
            transaction.customerReference(),
            transaction.status(),
            tags.stream().map(TransactionDetailController::toTransactionTagDto).toList());
    }

    static TransactionTagDto toTransactionTagDto(Tag tag) {
        return new TransactionTagDto(tag.name().value(), tag.color());
    }
}
