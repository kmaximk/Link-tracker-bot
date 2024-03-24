package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.entities.Chat;
import edu.java.scrapper.entities.Link;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    List<Link> getLinksByChatListContaining(Chat chat);

    Optional<Link> findByUrl(URI uri);
}
