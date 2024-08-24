package org.bseber.haushalt.development;

import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.bseber.haushalt.transactions.NewTransaction;
import org.bseber.haushalt.transactions.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Random;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

class DemoDataTransactionProvider {

    private static final List<String> PAYERS = List.of(
        "Myself",
        "ISSUER" // dkb csv files are defining this ISSUER name as payer
    );

    private static final List<String> PAYEES = List.of(
        "AMAZON PAYMENTS EUROPE S.C.A",
        "AMAZON EU S.A.R.L, NIEDERLASSUNG DEUTSCHLAND",
        "1+1 Telecom GmbH",
        "Telekom",
        "Frittenwerk",
        "Herr Vermieter",
        "Deutsche Bahn",
        "REWE",
        "Max - PayPal",
        "Martha - PayPal",
        "Robert - PayPal",
        "DoubleTree Hotel by Hilton",
        "Badische Backstub'",
        "APPLE.COM/BILL ITUNES.COM IE",
        "Apple",
        "dm-drogerie",
        "Dehner"
    );

    private static final List<String> IBANS = List.of(
        "DE111111111111",
        "DE222222222222",
        "DE333333333333",
        "DE444444444444",
        "DE555555555555",
        "DE666666666666",
        "DE777777777777"
    );

    private static final Random RANDOM = new Random();
    private static final Currency EUR = Currency.getInstance("EUR");

    NewTransaction createRandromTransaction(Month month) {

        final LocalDate firstDateOfMonth = LocalDate.now().withMonth(month.getValue()).with(firstDayOfMonth());
        final LocalDate lastDateOfMonth = LocalDate.now().withMonth(month.getValue()).with(lastDayOfMonth());
        final int days = lastDateOfMonth.getDayOfMonth() - firstDateOfMonth.getDayOfMonth();

        // TODO create transaction without valueDate AND status=BOOKED
        final LocalDate bookingDate = LocalDate.now().withMonth(month.getValue()).withDayOfMonth(random(days));
        final LocalDate valueDate = random(4) == 1 ? bookingDate : bookingDate.plusDays(1);

        return new NewTransaction(
            bookingDate,
            valueDate,
            random(Transaction.Procedure.values()),
            null,
            random(PAYERS),
            new IBAN(random(IBANS)),
            random(PAYEES),
            // TODO also create AMOUNT_COMING_IN
            Transaction.RevenueType.AMOUNT_COMING_OUT,
            new Money(BigDecimal.valueOf(random(500)).negate(), EUR),
            "awesome reference text",
            "",
            Transaction.Status.EXPENSED
        );
    }

    @SafeVarargs
    private static <T> T random(T... list) {
        return Arrays.asList(list).get(random(list.length) - 1);
    }

    private static <T> T random(List<T> list) {
        return list.get(random(list.size()) - 1);
    }

    private static int random(int max) {
        return RANDOM.nextInt(max) + 1;
    }
}
