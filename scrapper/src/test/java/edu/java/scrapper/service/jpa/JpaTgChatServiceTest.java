package edu.java.scrapper.service.jpa;

import edu.java.scrapper.controller.exceptions.ChatNotFoundException;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.repository.IntegrationEnvironment;
import java.net.URI;
import java.util.List;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql(value = "classpath:sql/cleanDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class JpaTgChatServiceTest extends IntegrationEnvironment {
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
    void addChatTest() {
        tgChatService.register(1000);
        assertEquals(
            tgChatRepository.findAll().stream().map(edu.java.scrapper.entities.Chat::getId).toList(),
            List.of(1000L)
        );
        tgChatService.unregister(1000L);
        assertEquals(0, tgChatRepository.findAll().size());
    }

    @Test
    void removeChatNotFoundTest() {
        tgChatService.register(1000);
        tgChatService.unregister(1000);
        assertThrows(ChatNotFoundException.class, () -> tgChatService.unregister(1000));
    }

    @Test
    void getChatsByLinkTest() {
        List<Long> chats = List.of(999L, 1000L, 1001L, 1002L);
        URI link = URI.create("https://github.com/owner/repo");
        tgChatService.register(999);
        assertTrue(tgChatRepository.findById(999L).isPresent());
        LinkModel linkModel = linkService.add(999L, link);
        for (int i = 1; i < chats.size(); i++) {
            tgChatService.register(chats.get(i));
            linkService.add(chats.get(i), link);
        }
        List<Long> chatsList = tgChatService.getChatsByLink(linkModel.id());
        List<Long> resultChats = new TreeSet<>(chatsList).stream().toList();
        assertEquals(chats, resultChats);
        for (Long chat : chats) {
            tgChatService.unregister(chat);
        }
        assertTrue(linkService.listAll(999).isEmpty());
        assertTrue(tgChatRepository.findAll().isEmpty());
    }
}
