package org.bseber.haushalt.bank.dkb;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.bseber.haushalt.pdf.PdfPage;
import org.bseber.haushalt.transactions.NewTransaction;
import org.bseber.haushalt.transactions.Transaction;
import org.bseber.haushalt.transactions.importer.AbstractTransactionPdfReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.invoke.MethodHandles.lookup;

@Service
class DkbPdfReader extends AbstractTransactionPdfReader {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    private static final Pattern ENDS_WITH_DATE_PATTERN = Pattern.compile(".+//[A-Z\\s]*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$");

    private record DkbPdfTransactionBlock(List<String> lines) {}

    @Override
    protected boolean isPdfSupported(File file) {
        // TODO any DKB specifics here?
        return true;
    }

    @Override
    protected List<NewTransaction> readPage(PdfPage page, AccountOwner accountOwner) {
        return toBlocks(page).stream().map(block -> toTransaction(block, accountOwner)).toList();
    }

    @Override
    protected AccountOwner getAccountOwner(PdfPage page) {

        // TODO is the name always line 7?
        final String name = page.lines().get(6);

        // TODO is the IBAN always line 3?
        final String line = page.lines().get(2);
        final String[] split = line.split(", ");
        final IBAN iban = new IBAN(split[1].replace(" ", ""));

        return new AccountOwner(name, iban);
    }

    private List<DkbPdfTransactionBlock> toBlocks(PdfPage page) {

        final List<DkbPdfTransactionBlock> blocks = new ArrayList<>();
        final Iterator<String> lineIterator = page.lines().iterator();

        while (lineIterator.hasNext()) {
            final String line = lineIterator.next();
            if (isFirstBlockLine(line)) {
                final List<String> blockLines = new ArrayList<>();
                blockLines.add(line);
                while (lineIterator.hasNext()) {
                    final String nextLine = lineIterator.next();
                    blockLines.add(nextLine);
                    if (isLastBlockLine(nextLine)) {
                        blocks.add(new DkbPdfTransactionBlock(blockLines));
                        break;
                    }
                }

            }
        }

        return blocks;
    }

    private static boolean isFirstBlockLine(String line) {
        // block starts with a date pattern `dd.MM.yyyy` followed by a word like "Basislastschrift" or "Kartenzahlung" or ...
        return Pattern.matches("^\\d{2}.\\d{2}.\\d{4}.+$", line);
    }

    private static boolean isLastBlockLine(String line) {
        // block ends with spaces and the amount
        return Pattern.matches("^\\s*-?(\\d+[,.]?)+$", line);
    }

    private static NewTransaction toTransaction(DkbPdfTransactionBlock block, AccountOwner accountOwner) {

        final Transaction.RevenueType revenueType = toRevenueType(block);
        final boolean comingIn = revenueType == Transaction.RevenueType.AMOUNT_COMING_IN;

        final LocalDate bookingDate = toBookingDate(block);
        final Optional<LocalDate> valueDate = Optional.of(bookingDate); // or empty? dunno
        final Transaction.Procedure procedure = toProcedure(block);
        final String payer = comingIn ? toName(block) : accountOwner.name();
        final String payee = comingIn ? accountOwner.name() : toName(block);
        final Optional<IBAN> payerIban = comingIn ? toIban(block) : Optional.of(accountOwner.iban());
        final Optional<IBAN> payeeIban = comingIn ? Optional.of(accountOwner.iban()) : toIban(block);
        final Money amount = toAmount(block);

        return new NewTransaction(bookingDate, valueDate, procedure, payerIban, payer, payeeIban, payee,
            revenueType, amount, "", "", Transaction.Status.EXPENSED);
    }

    private static LocalDate toBookingDate(DkbPdfTransactionBlock block) {
        final String line = block.lines.get(0);
        final String[] words = line.strip().split(" ");
        return toDate(words[0]).orElseThrow(() -> new IllegalStateException("Could not pares bookingDate from block."));
    }

    private static Transaction.Procedure toProcedure(DkbPdfTransactionBlock block) {

        final String line = block.lines.get(0);

        if (line.endsWith(" Kartenzahlung onl")) {
            return Transaction.Procedure.DEBIT_CARD_PAYMENT_ONLINE;
        }

        try {
            final String[] words = line.split(" ");
            final Transaction.Procedure type = switch (words[1]) {
                case "Kartenzahlung" -> Transaction.Procedure.DEBIT_CARD_PAYMENT;
                case "Zahlungseingang", "Überweisung", "Basislastschrift" -> Transaction.Procedure.TRANSFER;
                case "Dauerauftrag" -> Transaction.Procedure.STANDING_ORDER;
                case "Lohn," -> Transaction.Procedure.SALARY;
                default -> null;
            };
            if (type == null) {
                LOG.warn("Could not map '{}' to type", words[1]);
                return Transaction.Procedure.UNKNOWN;
            } else {
                return type;
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            LOG.info("Could not extract procedure from line. Using UNKNOWN.");
            return Transaction.Procedure.UNKNOWN;
        }
    }

    private static String toName(DkbPdfTransactionBlock block) {

        String nameCandidate = block.lines().get(1).strip();

        // replace stuff with "/../" separator handled in next step
        nameCandidate = nameCandidate
            .replace("/. ./", "/../")
            .replace("/.. /", "/../")
            .replace("/ .. /", "/../")
            // e.g. "REWE.Ponzer.GmbH../Karlsruhe"
            .replace("GmbH../", "/../")
            .strip();

        // e.g. "APPLE.COM.BILL/ITUNES.COM/../IE 2024-07-10T21:39"
        final int someSeparatorIndex = nameCandidate.indexOf("/../");
        if (someSeparatorIndex > -1) {
            nameCandidate = nameCandidate.substring(0, someSeparatorIndex).strip();
        }

        // e.g. "JAROONS DINER/KARLSRUHE//DE 2024-03-04T14:44"
        final int indexOfDebitPaymentDate = nameCandidate.lastIndexOf("//");
        if (indexOfDebitPaymentDate > -1 && ENDS_WITH_DATE_PATTERN.matcher(nameCandidate).matches()) {
            nameCandidate = nameCandidate.substring(0, indexOfDebitPaymentDate).strip();
        }

        return nameCandidate.strip();
    }

    private static Optional<IBAN> toIban(DkbPdfTransactionBlock block) {

        final List<String> lines = block.lines();

        for (int i = lines.size() - 1; i > 0; i--) {
            final String line = lines.get(i);
            final String prev = lines.get(i - 1);
            if (prev.endsWith("Gläubiger-ID:")) {
                return Optional.of(new IBAN(line));
            }
        }

        return Optional.empty();
    }

    private static Transaction.RevenueType toRevenueType(DkbPdfTransactionBlock block) {
        if (block.lines.get(0).contains("Zahlungseingang")) {
            return Transaction.RevenueType.AMOUNT_COMING_IN;
        } else {
            return Transaction.RevenueType.AMOUNT_COMING_OUT;
        }
    }

    private static Money toAmount(DkbPdfTransactionBlock block) {
        final String line = block.lines.get(block.lines.size() - 1);
        try {
            // replacing is true for german number values
            // well... works, since this is the use case currently
            final String string = line.strip().replace(".", "").replace(",", ".");
            final double value = Double.parseDouble(string);
            return Money.ofEUR(BigDecimal.valueOf(value));
        }
        catch(NullPointerException | NumberFormatException e) {
            LOG.warn("Could not parse amount.", e);
            return Money.ZERO_EUR;
        }
    }

    private static Optional<LocalDate> toDate(String line) {
        final String pattern = "dd.MM.yyyy";
        try {
            final String substring = line.substring(0, pattern.length());
            return Optional.of(LocalDate.parse(substring, DateTimeFormatter.ofPattern(pattern)));
        } catch (IndexOutOfBoundsException | DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
