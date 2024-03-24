package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.entities.Chat;
import edu.java.scrapper.entities.Link;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTgChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> getChatsByLinkListContaining(Link link);

}
