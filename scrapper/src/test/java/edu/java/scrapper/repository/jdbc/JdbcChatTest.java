package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.repository.IntegrationEnvironment;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@Transactional
public class JdbcChatTest extends IntegrationEnvironment {

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private  JdbcAssignmentRepository assignmentRepository;

    @Autowired
    private JdbcChatRepository chatRepository;

    @Test
    @Rollback
    void addChatTest() {
        chatRepository.add(1000);
        chatRepository.add(1001);

        assertTrue(chatRepository.containsChat(1000L));
        assertTrue(chatRepository.containsChat(1001L));
    }

    @Test
    @Rollback
    void removeChatTest() {
        chatRepository.add(1000);

        assertTrue(chatRepository.containsChat(1000L));
        chatRepository.remove(1000L);
        assertFalse(chatRepository.containsChat(1000L));
    }

    @Test
    @Rollback
    void addingLinksByChatTest() {
        chatRepository.add(1000);
        List<URI> uris = List.of(
            URI.create("http:test1"),
            URI.create("http:test2"),
            URI.create("http:test3")
        );

        uris.forEach(uri -> {
            LinkModel link = linkRepository.add(uri, OffsetDateTime.now(), 1);
            assignmentRepository.add(link.id(), 1000L);
        });

        List<LinkModel> links = assignmentRepository.findLinksByChat(1000L);

        assertEquals(3, links.size());
        assertEquals(uris, links.stream().map(LinkModel::url).sorted().toList());

        chatRepository.removeLinksByChat(1000);

        links = assignmentRepository.findLinksByChat(1000L);
        assertEquals(0, links.size());
    }

    @Test
    @Rollback
    void findChatsByLinkTest() {
        List<Long> chats = List.of(1000L, 1001L, 1002L);
        URI uri = URI.create("http:test10");
        LinkModel link = linkRepository.add(uri, OffsetDateTime.now(), 1);
        chats.forEach(chat -> {
            chatRepository.add(chat);
            assignmentRepository.add(link.id(), chat);
        });

        List<Long> foundChats = assignmentRepository.findChatsByLink(link.id());
        assertEquals(3, foundChats.size());
        Collections.sort(foundChats);
        assertEquals(chats, foundChats);
    }
}
