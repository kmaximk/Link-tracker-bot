package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repository.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcChatTest extends IntegrationEnvironment {
//    @BeforeAll
//    public static void start() {
//        DriverManagerDataSource driverManager = new DriverManagerDataSource();
//        driverManager.setDriverClassName("org.postgresql.Driver");
//        driverManager.setUrl(POSTGRES.getJdbcUrl());
//        driverManager.setUsername(POSTGRES.getUsername());
//        driverManager.setPassword(POSTGRES.getPassword());
//        JdbcClient client = JdbcClient.create(driverManager);
//        chatRepository = new JdbcChatRepository(client);
//        linkRepository = new JdbcLinkRepository(client);
//        assignmentRepository = new JdbcAssignmentRepository(client);
//    }

//    public static void start() {
//        linkRepository = new JdbcLinkRepository(jdbcClient);
//        assignmentRepository = new JdbcAssignmentRepository(jdbcClient);
//        chatRepository = new JdbcChatRepository(jdbcClient);
//    }

    @Test
    @Transactional
    @Rollback
    void addChatTest() {
        chatRepository.add(1000);
        chatRepository.add(1001);

        assertTrue(chatRepository.containsChat(1000L));
        assertTrue(chatRepository.containsChat(1001L));

        chatRepository.remove(1000);
        chatRepository.remove(1001);
    }

    @Test
    @Transactional
    @Rollback
    void removeChatTest() {
        chatRepository.add(1000);

        assertTrue(chatRepository.containsChat(1000L));
        chatRepository.remove(1000L);
        assertFalse(chatRepository.containsChat(1000L));
    }

    @Test
    @Transactional
    @Rollback
    void addingLinksByChatTest() {
        chatRepository.add(1000);
        List<URI> uris = List.of(
            URI.create("http:test1"),
            URI.create("http:test2"),
            URI.create("http:test3")
        );

        uris.forEach(uri -> {
            Link link = linkRepository.add(uri);
            assignmentRepository.add(link.id(), 1000L);
        });

        List<Link> links = assignmentRepository.findLinksByChat(1000L);

        assertEquals(3, links.size());
        assertEquals(uris, links.stream().map(Link::url).sorted().toList());

        chatRepository.removeLinksByChat(1000);

        links = assignmentRepository.findLinksByChat(1000L);
        assertEquals(0, links.size());
        chatRepository.remove(1000);
    }

    @Test
    @Transactional
    @Rollback
    void findChatsByLinkTest() {
        List<Long> chats = List.of(1001L, 1002L, 1003L);
        URI uri = URI.create("http:test");
        Link link = linkRepository.add(uri);
        chats.forEach(chat -> {
            chatRepository.add(chat);
            assignmentRepository.add(link.id(), chat);
        });

        List<Long> foundChats = assignmentRepository.findChatsByLink(link.id());
        assertEquals(3, foundChats.size());
        Collections.sort(foundChats);
        assertEquals(chats, foundChats);

        chats.forEach(chat -> {
            assignmentRepository.remove(link.id(), chat);
            chatRepository.remove(chat);
        });
    }
}
