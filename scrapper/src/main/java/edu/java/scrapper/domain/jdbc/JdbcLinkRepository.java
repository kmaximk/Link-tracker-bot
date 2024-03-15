package edu.java.scrapper.domain.jdbc;

import edu.java.dto.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository {

    private final JdbcClient jdbcClient;


    //@Override
    @Transactional
    public List<Link> findAll() {
        return jdbcClient.sql("""
            select * from link
            """).query(Link.class).list();
    }

    // @Override
    @Transactional
    public Link add(URI uri) {
        jdbcClient.sql("""
            insert into link (url, last_check_time, created_at)
            values(?, current_timestamp, current_timestamp);
            """).params(uri.toString()).update();
        return jdbcClient.sql("select * from link where url = ?").param(uri).query(Link.class).single();
    }

    @Transactional
    //@Override
    public int remove(URI uri) {
        return jdbcClient.sql("delete from link where url = :link").params(uri).update();

    }

    public Optional<Link> findLink(URI url) {
        return jdbcClient.sql("select * from link where url = ?").param(url).query(Link.class).optional();
    }
}
