package edu.java.scrapper.configuration;

import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.service.jdbc.JdbcLinkService;
import edu.java.scrapper.service.jdbc.JdbcTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public JdbcLinkService jdbcLinkService(
        JdbcAssignmentRepository assignmentRepository,
        JdbcLinkRepository linkRepository
    ) {
        return new JdbcLinkService(assignmentRepository, linkRepository);
    }

    @Bean
    public JdbcTgChatService jdbcTgChatService(
        JdbcChatRepository jdbcTgChatRepository,
        JdbcAssignmentRepository assignmentRepository
    ) {
        return new JdbcTgChatService(jdbcTgChatRepository, assignmentRepository);
    }
}
