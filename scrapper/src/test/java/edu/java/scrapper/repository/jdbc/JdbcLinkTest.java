package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repository.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Testcontainers
public class JdbcLinkTest extends IntegrationEnvironment {

    private static JdbcLinkRepository linkRepository;

    private static JdbcAssignmentRepository assignmentRepository;


    @BeforeAll
    static void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(POSTGRES.getJdbcUrl());
        dataSource.setUsername(POSTGRES.getUsername());
        dataSource.setPassword(POSTGRES.getPassword());
        assignmentRepository = new JdbcAssignmentRepository(JdbcClient.create(dataSource));
        linkRepository= new JdbcLinkRepository(JdbcClient.create(dataSource));
    }
    @Test
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
        linkRepository.remove(link);
    }

    @Test
    @Rollback
    public void getOutdatedLinksTest() {
        URI uri = URI.create("http:test");
        Link link = linkRepository.add(uri);
        OffsetDateTime now = OffsetDateTime.now().minusSeconds(5);
        linkRepository.updateLink(link.id(), now, now);
        List<Link> outdatedLinks = assignmentRepository.getOutdatedLinks(4L);
        List<Link> notOutdatedLinks = assignmentRepository.getOutdatedLinks(8L);
        System.out.println(outdatedLinks);
        assertEquals(1, outdatedLinks.size());
        assertEquals(outdatedLinks.getFirst().url(), uri);
        assertTrue(notOutdatedLinks.isEmpty());
        linkRepository.remove(uri);
    }
}
