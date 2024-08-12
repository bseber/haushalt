package org.bseber.haushalt.core;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Assert.notNull(amount, "Amount must not be null");
        Assert.notNull(currency, "Currency must not be null");
    }

    public static final Money ZERO_EUR = Money.ofEUR(BigDecimal.ZERO);

    /**
     * Create Money object with currency EUR.
     *
     * @param amount of money
     * @return money in EUR
     * @throws ParseException when amount cannot be parsed as number
     * @throws NumberFormatException when amount cannot be parsed as number
     */
    public static Money ofEUR(String amount) throws ParseException {
        final Number number = NumberFormat.getInstance().parse(amount);
        return ofEUR(number.doubleValue());
    }

    /**
     * Create Money object with currency EUR.
     *
     * @param amount of money
     * @return money in EUR
     * @throws NumberFormatException when amount cannot be parsed as number
     */
    public static Money ofEUR(double amount) throws NumberFormatException {
        return ofEUR(BigDecimal.valueOf(amount));
    }

    /**
     * Create Money object with currency EUR.
     *
     * @param amount of money
     * @return money in EUR
     */
    public static Money ofEUR(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("EUR"));
    }
}
