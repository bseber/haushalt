package org.bseber.haushalt.web.thymeleaf;

import org.bseber.haushalt.web.AssetManifestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@ConditionalOnClass({TemplateMode.class, SpringTemplateEngine.class})
class ThymeleafConfiguration implements WebMvcConfigurer {

    @Bean
    AssetDialect assetDialect(AssetManifestService assetManifestService) {
        return new AssetDialect(assetManifestService);
    }
}
