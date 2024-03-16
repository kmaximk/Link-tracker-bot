package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.controller.exceptions.LinkNotFoundException;
import edu.java.scrapper.controller.exceptions.ReAddingLinkException;
import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbsLinkService implements LinkService {

    private final JdbcChatRepository chatRepository;

    private final JdbcAssignmentRepository assignmentRepository;

    private final JdbcLinkRepository linkRepository;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {

        Link link = linkRepository.findLink(url).orElseGet(() -> linkRepository.add(url));
        if (assignmentRepository.linkIsTracked(link, tgChatId)) {
            throw new ReAddingLinkException(String.format("Link %s is already tracked", link.url()));
        }
        assignmentRepository.add(link.id(), tgChatId);
        return link;
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        Optional<Link> link = linkRepository.findLink(url);
        if (link.isEmpty() || assignmentRepository.remove(link.get().id(), tgChatId) == 0) {
            throw new LinkNotFoundException(String.format("Link %s is not tracked", url.toString()));
        }
        return link.get();
    }

    @Override
    public List<Link> listAll(long tgChatId) {
        return assignmentRepository.findLinksByChat(tgChatId);
    }
}
