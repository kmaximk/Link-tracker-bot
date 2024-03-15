package edu.java.scrapper.domain.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Types;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository {

    private final JdbcClient jdbcClient;

    public boolean containsChat(Long tgChatID) {
        return jdbcClient.sql("select count(*) from chat where id = ?").param(tgChatID).update() != 0;
    }

    // @Override
    public void add(long chatID) {
        jdbcClient.sql("insert into chat (id) values (:chatID)")
            .param("chatID", chatID, Types.BIGINT).update();
    }

    //@Override
    @Transactional
    public int remove(long chatID) {
        return jdbcClient.sql("delete from chat where id = :chatID")
            .param("chatID", chatID).update();
    }

    @Transactional
    public void removeLinksByChat(long chatID) {
        jdbcClient.sql("delete from assignment where chat_id = :chatID")
            .param("chatID", chatID).update();
    }
}
