package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.entities.Chat;
import edu.java.scrapper.entities.Link;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    List<Link> getLinksByChatListContaining(Chat chat);

    @EntityGraph(attributePaths = {"chatList"})
    Optional<Link> findByUrl(URI uri);

    @Query(value = "select * from link where extract(epoch from current_timestamp - last_check_time) > :interval",
           nativeQuery = true)
    List<Link> findOutdatedLinks(@Param("interval") Long interval);

    @EntityGraph(attributePaths = {"chatList"})
    @NotNull Optional<Link> findById(@NotNull Long id);

    @EntityGraph(attributePaths = {"chatList"})
    @NotNull List<Link> findAll();
}
