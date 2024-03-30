package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.entities.Chat;
import edu.java.scrapper.entities.Link;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTgChatRepository extends JpaRepository<Chat, Long> {

    @EntityGraph(attributePaths = {"linkList"})
    @NotNull Optional<Chat> findById(@NotNull Long id);

    List<Chat> getChatsByLinkListContaining(Link link);

}
