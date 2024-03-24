package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.repository.IntegrationEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
public class JdbcLinkTest extends IntegrationEnvironment {

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcAssignmentRepository assignmentRepository;

    @Test
    @Rollback
    public void addLinkTest() {
        URI link = URI.create("http:test1");
        linkRepository.add(link, OffsetDateTime.now(), 1);

        Optional<LinkModel> foundLink = linkRepository.findLink(link);

        assertTrue(foundLink.isPresent());
        assertEquals(link, foundLink.get().url());

        int updatedRows = linkRepository.remove(link);
        assertEquals(1, updatedRows);
        assertFalse(linkRepository.findLink(link).isPresent());
    }

    @Test
    @Rollback
    public void getOutdatedLinksTest() {
        URI uri = URI.create("http:test1");
        LinkModel link = linkRepository.add(uri, OffsetDateTime.now(), 1);
        OffsetDateTime newTime = OffsetDateTime.now().minusSeconds(5);
        linkRepository.updateLink(link.id(), newTime, newTime, 1);
        List<LinkModel> outdatedLinks = assignmentRepository.getOutdatedLinks(4L);
        List<LinkModel> notOutdatedLinks = assignmentRepository.getOutdatedLinks(8L);
        System.out.println(outdatedLinks);
        assertEquals(1, outdatedLinks.size());
        assertEquals(outdatedLinks.getFirst().url(), uri);
        assertEquals(1, outdatedLinks.getFirst().updatesCount());
        assertTrue(notOutdatedLinks.isEmpty());
    }
}
