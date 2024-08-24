package org.bseber.haushalt.bank.dkb;

import jakarta.annotation.Nullable;
import org.bseber.haushalt.bank.dkb.DkbCsvTransaktion.Status;
import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.bseber.haushalt.csv.CsvReader;
import org.bseber.haushalt.csv.CsvRow;
import org.bseber.haushalt.transactions.NewTransaction;
import org.bseber.haushalt.transactions.Transaction;
import org.bseber.haushalt.transactions.importer.TransactionFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.invoke.MethodHandles.lookup;
import static org.bseber.haushalt.DateParser.parseDate;
import static org.springframework.util.StringUtils.hasText;

@Component
class DkbCsvReader implements TransactionFileReader {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    private static final String DELIMITER = ";";
    private static final List<String> DATE_PATTERNS = List.of("dd.MM.yy", "dd.MM.yyyy");

    private final CsvReader csvReader;

    DkbCsvReader(CsvReader csvReader) {
        this.csvReader = csvReader;
    }

    @Override
    public boolean supports(File file) {
        // TODO any DKB specifics here?
        return file.getName().endsWith(".csv");
    }

    @Override
    public List<NewTransaction> readFile(File file) throws IOException {
        return csvReader.readRows(file, DELIMITER).stream()
            .map(DkbCsvReader::toDkbTransaktion)
            .filter(Objects::nonNull)
            .map(DkbCsvReader::toTransaction)
            .toList();
    }

    @Nullable
    private static DkbCsvTransaktion toDkbTransaktion(CsvRow row) {
        try {
            return new DkbCsvTransaktion(
                row.value(0, purge().andThen(DkbCsvReader::toDate)),
                row.value(1, purge().andThen(DkbCsvReader::toOptionalDate)),
                row.value(2, purge().andThen(DkbCsvReader::toStatus)),
                row.valueOrElse(3, purge(), ""),
                row.valueOrElse(4, purge(), ""),
                row.valueOrElse(5, purge(), ""),
                row.value(6, purge().andThen(DkbCsvReader::toType)),
                row.value(7, purge().andThen(IBAN::new)),
                row.value(8, purge().andThen(DkbCsvReader::toMoney)),
                row.valueOrElse(9, purge(), ""),
                row.valueOrElse(10, purge(), "")
            );
        } catch(RuntimeException e) {
            // row is logged, therefore guard with debug
            if (LOG.isDebugEnabled()) {
                LOG.error("could not create DkbTransaktion from csv row: {}", row);
            }
            return null;
        }
    }

    private static Function<String, String> purge() {
        return string -> string
            // remove leading and trailing quotes, whyever the DKB csv contains these
            .substring(1, string.length() - 1)
            // replace multiple spaces with only one
            .replaceAll("\\s{2,}", " ");
    }

    private static LocalDate toDate(String value) {
        return parseDate(value, DATE_PATTERNS);
    }

    private static Optional<LocalDate> toOptionalDate(String value) {
        return hasText(value) ? Optional.of(parseDate(value, DATE_PATTERNS)) : Optional.empty();
    }

    private static Status toStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.warn("could not map status={} to a known Status enum.", status);
            return null;
        }
    }

    private static Money toMoney(String money) {
        try {
            return Money.ofEUR(money);
        } catch (ParseException | NumberFormatException e) {
            LOG.warn("Could not parse '{}' to a Money value Using Money of value 0.", money);
            return Money.ZERO_EUR;
        }
    }

    private static DkbCsvTransaktion.Type toType(String type) {
        try {
            return DkbCsvTransaktion.Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.warn("could not map type={} to a known Type enum.", type);
            return null;
        }
    }

    private static NewTransaction toTransaction(DkbCsvTransaktion dkbTransaktion) {
        return new NewTransaction(
            dkbTransaktion.bookingDate(),
            dkbTransaktion.valueDate().orElse(null),
            toProcedure(dkbTransaktion),
            null,
            dkbTransaktion.payer(),
            dkbTransaktion.iban(),
            dkbTransaktion.payee(),
            toTyp(dkbTransaktion.type()),
            dkbTransaktion.amount(),
            dkbTransaktion.reference(),
            dkbTransaktion.customerReference(),
            toStatus(dkbTransaktion.status())
        );
    }

    private static Transaction.Procedure toProcedure(DkbCsvTransaktion dkbTransaktion) {
        if (dkbTransaktion.reference().equals("VISA Debitkartenumsatz")) {
            LOG.debug("using Vorgang DEBITKARTENZAHLUNG for transaction {}", dkbTransaktion);
            return Transaction.Procedure.DEBIT_CARD_PAYMENT;
        }
        LOG.debug("using Vorgang UNKNOWN for transaction {}", dkbTransaktion);
        return Transaction.Procedure.UNKNOWN;
    }

    private static Transaction.RevenueType toTyp(DkbCsvTransaktion.Type type) {
        return switch (type) {
            case AUSGANG -> Transaction.RevenueType.AMOUNT_COMING_OUT;
            case EINGANG -> Transaction.RevenueType.AMOUNT_COMING_IN;
        };
    }

    private static Transaction.Status toStatus(DkbCsvTransaktion.Status status) {
        return switch (status) {
            case GEBUCHT -> Transaction.Status.EXPENSED;
            case VORGEMERKT -> Transaction.Status.BOOKED;
        };
    }
}
