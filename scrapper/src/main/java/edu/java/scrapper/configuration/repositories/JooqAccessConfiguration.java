package edu.java.scrapper.configuration.repositories;

import edu.java.scrapper.service.jooq.JooqLinkService;
import edu.java.scrapper.service.jooq.JooqTgChatService;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {

    @Bean
    public JooqLinkService jooqLinkService(DSLContext dslContext) {
        return new JooqLinkService(dslContext);
    }

    @Bean
    public JooqTgChatService jooqTgChatService(DSLContext dslContext) {
        return new JooqTgChatService(dslContext);
    }
}
