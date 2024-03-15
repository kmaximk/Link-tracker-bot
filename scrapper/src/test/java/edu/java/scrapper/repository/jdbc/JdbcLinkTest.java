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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JdbcLinkTest extends IntegrationEnvironment {
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcChatRepository chatRepository;
    @Autowired
    private JdbcClient jdbcClient;


    @Test
    @Transactional
    @Rollback
    void findAllLinkTest() {
//        chatRepository.add(1000);
//        linkRepository.add(URI.create("http:localhost"), 1000);
//        List<Link> result = linkRepository.findAll(1000);
//        assertEquals(result.size(), 1);
//        assertEquals(result.getFirst().url(), URI.create("http:localhost"));
    }
    @Test
    @Transactional
    @Rollback
    void addLinkTest() {
//        chatRepository.add(1000);
//        linkRepository.add(URI.create("http:localhost"), 1000);
//        List<Link> result = linkRepository.findAll(1000);
//        assertEquals(result.size(), 1);
//        assertEquals(result.getFirst().url(), URI.create("http:localhost"));
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkTest() {
    }
}
