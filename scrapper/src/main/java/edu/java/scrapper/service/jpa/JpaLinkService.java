package edu.java.scrapper.service.jpa;

import edu.java.scrapper.controller.exceptions.LinkNotFoundException;
import edu.java.scrapper.controller.exceptions.ReAddingLinkException;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.entities.Chat;
import edu.java.scrapper.entities.Link;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {

    private final JpaLinkRepository linkRepository;

    private final JpaTgChatRepository chatRepository;

    private static final String LINK_NOT_TRACKED = "Link %s is not tracked";

    @Override
    @Transactional
    public LinkModel add(long tgChatId, URI url) {
        Chat chat = chatRepository.findById(tgChatId).orElseThrow();
        Link link = linkRepository.findByUrl(url).orElseGet(() -> linkRepository.save(
            new Link(url, OffsetDateTime.now(), OffsetDateTime.now(), 1)));
        if (chat.getLinkList().stream().anyMatch(l -> l.getId().equals(link.getId()))) {
            throw new ReAddingLinkException(String.format("Link %s is already tracked", link.getUrl()));
        }
        if (link.getChatList() != null) {
            link.getChatList().add(chat);
        }
        chat.getLinkList().add(link);
        chatRepository.save(chat);
        return convertToModel(link);
    }

    @Override
    @Transactional
    public LinkModel remove(long tgChatId, URI url) {
        Link link = linkRepository.findByUrl(url).orElseThrow(
            () -> new LinkNotFoundException(String.format(LINK_NOT_TRACKED, url)));
        if (!link.getChatList().removeIf(c -> c.getId().equals(tgChatId))) {
            throw new LinkNotFoundException(String.format(LINK_NOT_TRACKED, url));
        }
        linkRepository.save(link);
        return convertToModel(link);
    }

    @Override
    @Transactional
    public List<LinkModel> listAll(long tgChatId) {
        Optional<Chat> chat = chatRepository.findById(tgChatId);
        return chat.map(value -> linkRepository.getLinksByChatListContaining(value).stream().map(this::convertToModel)
            .toList()).orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public void updateLink(
        LinkModel link,
        OffsetDateTime checkTime,
        OffsetDateTime updatedAt,
        Integer updatesCount
    ) {
        Link foundLink = linkRepository.findById(link.id()).orElseThrow();
        foundLink.setLastCheckTime(checkTime);
        foundLink.setUpdatedAt(updatedAt);
        foundLink.setUpdatesCount(updatesCount);
        linkRepository.save(foundLink);
    }

    @Override
    @Transactional
    public List<LinkModel> getOutdatedLinks(Long interval) {
        return linkRepository.findOutdatedLinks(interval)
            .stream()
            .map(this::convertToModel)
            .toList();
    }

    private LinkModel convertToModel(Link link) {
        return new LinkModel(
            link.getId(),
            link.getUrl(),
            link.getLastCheckTime(),
            link.getUpdatedAt(),
            link.getUpdatesCount()
        );
    }
}
