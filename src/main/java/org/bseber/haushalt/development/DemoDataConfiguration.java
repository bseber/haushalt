package org.bseber.haushalt.development;

import org.bseber.haushalt.transactions.TransactionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "application.demo.data.create", havingValue = "true")
class DemoDataConfiguration {

    @Bean
    DemoDataCreator demoDataCreator(TransactionService transactionService) {
        return new DemoDataCreator(demoDataTransactionProvider(), transactionService);
    }

    @Bean
    DemoDataTransactionProvider demoDataTransactionProvider() {
        return new DemoDataTransactionProvider();
    }
}
