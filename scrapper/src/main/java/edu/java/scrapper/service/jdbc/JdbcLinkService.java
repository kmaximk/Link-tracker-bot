package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.controller.exceptions.LinkNotFoundException;
import edu.java.scrapper.controller.exceptions.ReAddingLinkException;
import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final JdbcAssignmentRepository assignmentRepository;

    private final JdbcLinkRepository linkRepository;

    @Override
    @Transactional
    public LinkModel add(long tgChatId, URI url) {
        LinkModel link = linkRepository.findLink(url)
                .orElseGet(() -> linkRepository.add(url, OffsetDateTime.now(), 0));
        if (assignmentRepository.linkIsTracked(link, tgChatId)) {
            throw new ReAddingLinkException(String.format("Link %s is already tracked", link.url()));
        }
        assignmentRepository.add(link.id(), tgChatId);
        return link;
    }

    @Override
    @Transactional
    public LinkModel remove(long tgChatId, URI url) {
        Optional<LinkModel> link = linkRepository.findLink(url);
        if (link.isEmpty() || assignmentRepository.remove(link.get().id(), tgChatId) == 0) {
            throw new LinkNotFoundException(String.format("Link %s is not tracked", url));
        }
        return link.get();
    }

    @Override
    @Transactional
    public List<LinkModel> listAll(long tgChatId) {
        return assignmentRepository.findLinksByChat(tgChatId);
    }

    @Override
    @Transactional
    public void updateLink(
        LinkModel link, OffsetDateTime checkTime,
        OffsetDateTime updatedAt, Integer updatesCount
    ) {
        linkRepository.updateLink(
            link.id(),
            checkTime,
            updatedAt,
            updatesCount
        );
    }
}
