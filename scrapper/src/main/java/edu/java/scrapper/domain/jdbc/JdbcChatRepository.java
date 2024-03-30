package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.TgChatRepository;
import java.sql.Types;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements TgChatRepository {

    private final JdbcClient jdbcClient;

    private static final String CHAT_ID = "chatID";

    @Override
    public boolean containsChat(Long tgChatID) {
        return jdbcClient.sql("select count(*) from chat where id = ?")
            .param(tgChatID)
            .query(Long.class)
            .single() != 0;
    }

     @Override
    @Transactional
    public void add(long chatID) {
        jdbcClient.sql("insert into chat (id) values (:chatID)")
            .param(CHAT_ID, chatID, Types.BIGINT)
            .update();
    }

    @Override
    public int remove(long chatID) {
        return jdbcClient.sql("delete from chat where id = :chatID")
            .param(CHAT_ID, chatID)
            .update();
    }

    @Override
    public void removeLinksByChat(long chatID) {
        jdbcClient.sql("delete from assignment where chat_id = :chatID")
            .param(CHAT_ID, chatID)
            .update();
    }
}
