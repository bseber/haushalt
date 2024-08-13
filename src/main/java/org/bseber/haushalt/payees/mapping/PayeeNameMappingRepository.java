package org.bseber.haushalt.payees.mapping;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface PayeeNameMappingRepository extends CrudRepository<PayeeNameMappingEntity, Long> {

    @Query("select distinct t.payee from transaction t")
    List<String> findAllPayeeNamesDistinctFromTransactions();
}
