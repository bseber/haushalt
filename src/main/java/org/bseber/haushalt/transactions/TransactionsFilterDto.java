package org.bseber.haushalt.transactions;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

record TransactionsFilterDto(

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate from,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate to
) {
}
