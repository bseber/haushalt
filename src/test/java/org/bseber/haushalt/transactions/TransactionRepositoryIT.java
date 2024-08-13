package org.bseber.haushalt.transactions;

import org.bseber.haushalt.payees.mapping.PayeeNameMapping;
import org.bseber.haushalt.payees.mapping.PayeeNameMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
class TransactionRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("haushalt")
        .withUsername("haushalt")
        .withPassword("secret");

    @Autowired
    private TransactionRepository sut;

    @Autowired
    private PayeeNameMappingService payeeNameMappingService;

    @Test
    void ensureProjectionWithMappedPayee() {

        final LocalDate bookingDate = LocalDate.of(2024, 8, 10);

        final TransactionEntity entity = anyTransactionEntity();
        entity.setBookingDate(bookingDate);
        entity.setPayee("Amazon Blubb di hubb du dubb");

        sut.save(entity);

        payeeNameMappingService.updateNameMappings(List.of(
            new PayeeNameMapping("Amazon Blubb di hubb du dubb", "Amazon"))
        );

        assertThat(sut.findAll()).hasSize(1);

        final List<TransactionEntity> actual = sut.findAllByBookingDateAfterAndBookingDateBefore(bookingDate.minusDays(1), bookingDate.plusDays(1));
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getPayee()).isEqualTo("Amazon");
    }

    @Test
    void ensureProjectionWhenThereAreNoMappingsYet() {

        final LocalDate bookingDate = LocalDate.of(2024, 8, 10);

        final TransactionEntity entity = anyTransactionEntity();
        entity.setBookingDate(bookingDate);
        entity.setPayee("Amazon Blubb di hubb du dubb");

        sut.save(entity);

        assertThat(sut.findAll()).hasSize(1);

        final List<TransactionEntity> actual = sut.findAllByBookingDateAfterAndBookingDateBefore(bookingDate.minusDays(1), bookingDate.plusDays(1));
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getPayee()).isEqualTo("Amazon Blubb di hubb du dubb");
    }

    private static TransactionEntity anyTransactionEntity() {
        final TransactionEntity entity = new TransactionEntity();
        entity.setBookingDate(LocalDate.now());
        entity.setValueDate(LocalDate.now());
        entity.setProcedure(Transaction.Procedure.UNKNOWN);
        entity.setIbanPayer(null);
        entity.setPayer("Ich");
        entity.setIbanPayee("DE111111111111");
        entity.setPayee("payee");
        entity.setAmount(BigDecimal.valueOf(4.99));
        entity.setCurrency("EUR");
        entity.setRevenueType(Transaction.RevenueType.AMOUNT_COMING_OUT);
        entity.setReference("");
        entity.setCustomerReference("");
        entity.setStatus(Transaction.Status.BOOKED);
        return entity;
    }
}
