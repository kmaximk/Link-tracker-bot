package edu.java.scrapper.service.jpa;

import edu.java.scrapper.controller.exceptions.ChatNotFoundException;
import edu.java.scrapper.controller.exceptions.ReRegistrationException;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.entities.Chat;
import edu.java.scrapper.entities.Link;
import edu.java.scrapper.service.TgChatService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaTgChatService implements TgChatService {

    private final JpaLinkRepository linkRepository;

    private final JpaTgChatRepository chatRepository;

    @Override
    @Transactional
    public void register(long tgChatId) {
        if (chatRepository.existsById(tgChatId)) {
            throw new ReRegistrationException(String.format("Chat %d already exists", tgChatId));
        }
        Chat chatEntity = new Chat();
        chatEntity.setId(tgChatId);
        chatRepository.save(chatEntity);
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        if (!chatRepository.existsById(tgChatId)) {
            throw new ChatNotFoundException(String.format("Chat %d not found", tgChatId));
        }
        chatRepository.deleteById(tgChatId);
    }

    @Override
    @Transactional
    public List<Long> getChatsByLink(Long linkID) {
        Optional<Link> linkEntity = linkRepository.findById(linkID);
        return linkEntity.map(entity -> chatRepository.getChatsByLinkListContaining(entity)
                .stream()
                .map(Chat::getId)
                .toList())
            .orElse(Collections.emptyList());
    }
}
