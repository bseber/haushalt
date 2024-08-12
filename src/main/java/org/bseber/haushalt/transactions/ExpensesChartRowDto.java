package org.bseber.haushalt.transactions;

import java.math.BigDecimal;

record ExpensesChartRowDto(String month, String payee, BigDecimal amount) {
}
