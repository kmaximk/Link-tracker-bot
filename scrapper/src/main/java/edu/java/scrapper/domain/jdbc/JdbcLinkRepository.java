package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.models.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {

    private final JdbcClient jdbcClient;

    private static final String LINK_BY_URL = "select * from link where url = ?";

    @Override
    public List<Link> findAll() {
        return jdbcClient.sql("""
                select * from link
                """)
            .query(Link.class)
            .list();
    }

    @Override
    @Transactional
    public Link add(URI uri) {
        jdbcClient.sql("""
            insert into link (url, last_check_time, updated_at)
            values(?, current_timestamp, current_timestamp);
            """).params(uri.toString()).update();
        return jdbcClient.sql(LINK_BY_URL)
            .param(uri.toString())
            .query(Link.class)
            .single();
    }

    @Override
    public int remove(URI uri) {
        return jdbcClient.sql("delete from link where url = ?")
            .param(uri.toString())
            .update();

    }

    @Override
    public Optional<Link> findLink(URI url) {
        return jdbcClient.sql(LINK_BY_URL)
            .param(url.toString())
            .query(Link.class)
            .optional();
    }

    @Override
    @Transactional
    public void updateLink(Long linkID, OffsetDateTime checkTime, OffsetDateTime updatedAt) {
        jdbcClient.sql("update link set last_check_time = ?, updated_at = ? where id = ?")
            .params(checkTime, updatedAt, linkID)
            .update();
    }
}
