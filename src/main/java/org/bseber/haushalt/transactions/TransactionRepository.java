package org.bseber.haushalt.transactions;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    @Query("""
        select t.*,
               COALESCE(m.mapped_name, '') as mapped_payee
        from transaction t
          left join payee_name_mapping m on t.payee = m.original_name
        where t.id = :id
        """)
    Optional<TransactionEntityProjection> findProjectionById(Long id);

    @Query("""
        select t.*,
               COALESCE(m.mapped_name, '') as mapped_payee
        from transaction t
          left join payee_name_mapping m on t.payee = m.original_name
        where t.booking_date > :after and t.booking_date < :before
        """)
    List<TransactionEntityProjection> findAllByBookingDateAfterAndBookingDateBefore(LocalDate after, LocalDate before);
}
