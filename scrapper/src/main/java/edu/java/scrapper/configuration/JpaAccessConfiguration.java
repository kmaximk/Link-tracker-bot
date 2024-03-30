package edu.java.scrapper.configuration;

import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.service.jpa.JpaLinkService;
import edu.java.scrapper.service.jpa.JpaTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@EnableJpaRepositories
public class JpaAccessConfiguration {

    @Bean
    public JpaLinkService jpaLinkService(
        JpaLinkRepository linkRepository,
        JpaTgChatRepository tgChatRepository
    ) {
        return new JpaLinkService(linkRepository, tgChatRepository);
    }

    @Bean
    public JpaTgChatService jpaTgChatService(
        JpaLinkRepository linkRepository,
        JpaTgChatRepository tgChatRepository
    ) {
        return new JpaTgChatService(linkRepository, tgChatRepository);
    }
}
