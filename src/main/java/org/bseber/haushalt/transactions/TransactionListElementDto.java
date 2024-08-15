package org.bseber.haushalt.transactions;

import java.math.BigDecimal;
import java.time.LocalDate;

record TransactionListElementDto(Long id, String payee, String reference, BigDecimal amount, LocalDate bookingDate, boolean active) {
}
