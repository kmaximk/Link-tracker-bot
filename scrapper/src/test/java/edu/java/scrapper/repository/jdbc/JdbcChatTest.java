package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.IntegrationEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JdbcChatTest extends IntegrationEnvironment {
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcChatRepository chatRepository;
    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @Transactional
    @Rollback
    void addChatTest() {
        chatRepository.add(1000);
        int res = chatRepository.add(1001);
        assertEquals(res, 1);
        res = chatRepository.add(1001);
        assertEquals(res, 0);
        List<Long> result = chatRepository.findAll();
        assertEquals(result.size(), 2);
        assertTrue(result.contains(1000L));
        assertTrue(result.contains(1001L));
    }

    @Test
    @Transactional
    @Rollback
    void removeChatTest() {
        chatRepository.add(1000);
        int updated = chatRepository.remove(10);
        assertEquals(updated, 0);
    }
}
