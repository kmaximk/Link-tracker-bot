package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.models.LinkModel;
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
    public List<LinkModel> findAll() {
        return jdbcClient.sql("""
                select * from link
                """)
            .query(LinkModel.class)
            .list();
    }

    @Override
    @Transactional
    public LinkModel add(URI uri, OffsetDateTime updatedAt, Integer updatesCount) {
        jdbcClient.sql("""
            insert into link (url, last_check_time, updated_at, updates_count)
            values(?, current_timestamp, ?, ?);
            """).params(uri.toString(), updatedAt, updatesCount).update();
        return jdbcClient.sql(LINK_BY_URL)
            .param(uri.toString())
            .query(LinkModel.class)
            .single();
    }

    @Override
    public int remove(URI uri) {
        return jdbcClient.sql("delete from link where url = ?")
            .param(uri.toString())
            .update();

    }

    @Override
    public Optional<LinkModel> findLink(URI url) {
        return jdbcClient.sql(LINK_BY_URL)
            .param(url.toString())
            .query(LinkModel.class)
            .optional();
    }

    @Override
    @Transactional
    public void updateLink(Long linkID, OffsetDateTime checkTime, OffsetDateTime updatedAt, Integer updatesCount) {
        jdbcClient.sql("update link set last_check_time = ?, updated_at = ?, updates_count = ? where id = ?")
            .params(checkTime, updatedAt, updatesCount, linkID)
            .update();
    }
}
