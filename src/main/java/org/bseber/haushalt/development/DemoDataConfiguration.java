package org.bseber.haushalt.development;

import org.bseber.haushalt.tags.TagService;
import org.bseber.haushalt.transactions.TransactionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "application.demo.data.create", havingValue = "true")
class DemoDataConfiguration {

    @Bean
    DemoDataCreator demoDataCreator(TransactionService transactionService, TagService tagService) {
        return new DemoDataCreator(demoDataTransactionProvider(), demoDataTagProvider(), transactionService, tagService);
    }

    @Bean
    DemoDataTransactionProvider demoDataTransactionProvider() {
        return new DemoDataTransactionProvider();
    }

    @Bean
    DemoDataTagProvider demoDataTagProvider() {
        return new DemoDataTagProvider();
    }
}
