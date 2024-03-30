package edu.java.scrapper.service.jpa;

import edu.java.scrapper.controller.exceptions.LinkNotFoundException;
import edu.java.scrapper.controller.exceptions.ReAddingLinkException;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.entities.Link;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.repository.IntegrationEnvironment;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(value = "classpath:sql/cleanDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class JpaLinkServiceTest extends IntegrationEnvironment {
    private static JpaLinkService linkService;

    private static JpaTgChatService tgChatService;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaTgChatRepository tgChatRepository;

    @BeforeEach
    public void init() {
        tgChatService = new JpaTgChatService(linkRepository, tgChatRepository);
        linkService = new JpaLinkService(linkRepository, tgChatRepository);
    }

    @Test
    public void getOutdatedLinksTest() {
        List<URI> uris = List.of(
            URI.create("https://github.com/owner/repo"),
            URI.create("https://github.com/owner/repo1")
        );
        long chatID = 1000L;
        tgChatService.register(chatID);
        LinkModel link = linkService.add(1000, uris.getFirst());
        linkService.updateLink(link, OffsetDateTime.now().minusSeconds(5), link.updatedAt(), link.updatesCount());
        linkService.add(chatID, uris.get(1));
        List<LinkModel> outdatedLinks = linkService.getOutdatedLinks(1L);
        assertEquals(1, outdatedLinks.size());
        assertEquals(link.id(), outdatedLinks.getFirst().id());
    }

    @Test
    public void addExistingLinkTest() {
        long chatID = 1000L;
        URI link = URI.create("https://github.com/owner/repo");
        tgChatService.register(chatID);
        linkService.add(chatID, link);
        List<Link> links = linkRepository.findAll();
        assertEquals(1, links.size());
        assertEquals(1000L, links.getFirst().getChatList().getFirst().getId());
        assertThrows(ReAddingLinkException.class, () -> linkService.add(chatID, link));
    }


    @Test
    public void removeUntrackedLinkTest() {
        long chatID = 1000L;
        URI link = URI.create("https://github.com/owner/repo");
        tgChatService.register(1000);
        tgChatService.register(1001);
        assertThrows(LinkNotFoundException.class, () -> linkService.remove(chatID, link));
        linkService.add(1001L, link);
        assertThrows(LinkNotFoundException.class, () -> linkService.remove(chatID, link));
    }
}
