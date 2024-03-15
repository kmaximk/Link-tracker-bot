package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.AssignmentRepository;
import edu.java.dto.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcAssignmentRepository implements AssignmentRepository {

    private final JdbcClient jdbcClient;

    @Override
    public List<Long> findChatsByLink(Long linkID) {
        return jdbcClient.sql("select * from assignment where link_id = ?").param(linkID).query(Long.class).list();
    }

    @Override
    public List<Link> findLinksByChat(Long chatID) {
        return jdbcClient.sql("select * from assignment where chat_id = ?").param(chatID).query(Link.class).list();
    }

    @Override
    public void add(Long linkID, Long chatID) {
        jdbcClient.sql("insert into assignment (chat_id, link_id) values (?, ?)").params(chatID, linkID)
            .update();
    }

    @Override
    public int remove(Long linkID, Long chatID) {
        return jdbcClient.sql("delete from assignment where link_id = ? and chat_id = ?").params(chatID, linkID)
            .update();
    }

    public boolean linkIsTracked(Link link, Long userID) {
        return jdbcClient.sql("""
            select count(*) from assignment where chat_id = ? and link_id = ?
            """).params(userID, link.id()).update() != 0;
    }
}
