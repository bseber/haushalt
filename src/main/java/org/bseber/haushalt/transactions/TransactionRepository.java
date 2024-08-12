package org.bseber.haushalt.transactions;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByBookingDateAfterAndBookingDateBefore(LocalDate after, LocalDate before);
}
