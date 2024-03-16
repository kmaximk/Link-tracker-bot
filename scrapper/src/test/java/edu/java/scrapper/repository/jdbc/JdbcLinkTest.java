package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repository.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcLinkTest extends IntegrationEnvironment {

//    @Autowired
//    private JdbcLinkRepository linkRepository;
//
//    @Autowired
//    private JdbcAssignmentRepository assignmentRepository;
//
//    @Autowired
//    private JdbcChatRepository chatRepository;

//    @Autowired
//    private JdbcClient jdbcClient;

//    @BeforeAll
//    public static void start() {
//        DriverManagerDataSource driverManager = new DriverManagerDataSource();
//        driverManager.setDriverClassName("org.postgresql.Driver");
//        driverManager.setUrl(POSTGRES.getJdbcUrl());
//        driverManager.setUsername("postgres");
//        driverManager.setPassword("postgres");
//        chatRepository = new JdbcChatRepository(JdbcClient.create(driverManager));
//        linkRepository = new JdbcLinkRepository(JdbcClient.create(driverManager));
//        assignmentRepository = new JdbcAssignmentRepository(JdbcClient.create(driverManager));
//    }
//    @BeforeAll
//    public static void start(){
//        linkRepository = new JdbcLinkRepository(jdbcClient);
//        assignmentRepository = new JdbcAssignmentRepository(jdbcClient);
//        chatRepository = new JdbcChatRepository(jdbcClient);
//    }


    @Test
    @Transactional
    @Rollback
    public void addLinkTest() {
        URI link = URI.create("http:test1");
        linkRepository.add(link);

        Optional<Link> foundLink = linkRepository.findLink(link);

        assertTrue(foundLink.isPresent());
        assertEquals(link, foundLink.get().url());

        int updatedRows = linkRepository.remove(link);
        assertEquals(1, updatedRows);
        assertFalse(linkRepository.findLink(link).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    public void getOutdatedLinksTest() {
        URI uri = URI.create("http:test");
        Link link = linkRepository.add(uri);
        OffsetDateTime now = OffsetDateTime.now().minusSeconds(2);
        linkRepository.updateLink(link.id(), now, now);
        List<Link> outdatedLinks = assignmentRepository.getOutdatedLinks(1L);
        List<Link> notOutdatedLinks = assignmentRepository.getOutdatedLinks(3L);

        assertEquals(1, outdatedLinks.size());
        assertEquals(outdatedLinks.getFirst().url(), uri);
        assertTrue(notOutdatedLinks.isEmpty());
    }
}
