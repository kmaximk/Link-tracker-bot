package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.AssignmentRepository;
import edu.java.scrapper.models.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcAssignmentRepository implements AssignmentRepository {

    private final JdbcClient jdbcClient;

    @Override
    public List<Long> findChatsByLink(Long linkID) {
        return jdbcClient.sql("select chat_id from assignment where link_id = ?")
            .param(linkID)
            .query(Long.class)
            .list();
    }

    @Override
    public List<Link> findLinksByChat(Long chatID) {
        return jdbcClient.sql("""
                select * from link where id in
                (select link_id from assignment where chat_id = ?)
                """)
            .param(1, chatID, Types.BIGINT)
            .query(Link.class)
            .list();
    }

    @Override
    @Transactional
    public void add(Long linkID, Long chatID) {
        jdbcClient.sql("insert into assignment (chat_id, link_id) values (?, ?)")
            .params(chatID, linkID)
            .update();
    }

    @Override
    public int remove(Long linkID, Long chatID) {
        return jdbcClient.sql("delete from assignment where link_id = ? and chat_id = ?")
            .params(linkID, chatID)
            .update();
    }

    public boolean linkIsTracked(Link link, Long userID) {
        return jdbcClient.sql("""
                select count(*) from assignment where chat_id = ? and link_id = ?
                """)
            .params(userID, link.id())
            .query(Long.class)
            .single() != 0;
    }

    public List<Link> getOutdatedLinks(Long interval) {
        return jdbcClient.sql("select * from link " +
                "where last_check_time < current_timestamp - interval '" + interval + " second'")
            .query(Link.class).list();
    }
}
