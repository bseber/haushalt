package org.bseber.haushalt.transactions;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    @Query("""
        select t.booking_date,
               t.amount,
               t.currency,
               t.customer_reference,
               t.iban_payee,
               t.iban_payer,
               t.id,
               t.payer,
               t.procedure,
               t.reference,
               t.revenue_type,
               t.status,
               t.value_date,
               COALESCE(NULLIF(m.mapped_name, ''), t.payee) as payee
        from transaction t
          left join payee_name_mapping m on t.payee = m.original_name
        where t.booking_date > :after and t.booking_date < :before
        """)
    List<TransactionEntity> findAllByBookingDateAfterAndBookingDateBefore(LocalDate after, LocalDate before);
}
