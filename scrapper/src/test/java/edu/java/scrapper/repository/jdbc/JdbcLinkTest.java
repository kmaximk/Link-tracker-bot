package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.IntegrationEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JdbcLinkTest extends IntegrationEnvironment {
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcChatRepository chatRepository;

    @Autowired
    private JdbcAssignmentRepository assignmentRepository;

    @Autowired
    private JdbcClient jdbcClient;

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
    public void getOutdatedLinksTest() throws InterruptedException, IOException {
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
