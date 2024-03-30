package edu.java.scrapper.service.jooq;

import edu.java.scrapper.controller.exceptions.ChatNotFoundException;
import edu.java.scrapper.domain.jooq.tables.Chat;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.repository.IntegrationEnvironment;
import edu.java.scrapper.service.updaters.Updater;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(value = "classpath:sql/cleanDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class JooqTgChatServiceTest extends IntegrationEnvironment {

    private JooqTgChatService tgChatService;

    @Autowired
    private DSLContext context;

    private JooqLinkService linkService;

    @BeforeEach
    public void init() {
        tgChatService = new JooqTgChatService(context);
        linkService = new JooqLinkService(context);
    }

    @Test
    void addChatTest() {
        tgChatService.register(1000);
        assertEquals(1, context.fetchCount(Chat.CHAT, Chat.CHAT.ID.eq(1000L)));
        tgChatService.unregister(1000L);
        assertEquals(0, context.fetchCount(Chat.CHAT, Chat.CHAT.ID.eq(1000L)));
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
        LinkModel linkModel = linkService.add(999L, link);
        for (int i = 1; i < chats.size(); i++) {
            tgChatService.register(chats.get(i));
            linkService.add(chats.get(i), link);
        }
        List<Long> resultChats = tgChatService.getChatsByLink(linkModel.id());
        Collections.sort(resultChats);
        assertEquals(chats, resultChats);
    }
}
