package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.controller.exceptions.ChatNotFoundException;
import edu.java.scrapper.controller.exceptions.ReRegistrationException;
import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.service.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbsTgChatService implements TgChatService {

    private final JdbcChatRepository chatRepository;

    @Override
    @Transactional
    public void register(long tgChatId) {
        if (chatRepository.containsChat(tgChatId)) {
            throw new ReRegistrationException(String.format("Chat %d already exists", tgChatId));
        }
        chatRepository.add(tgChatId);
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        chatRepository.removeLinksByChat(tgChatId);
        if (chatRepository.remove(tgChatId) == 0) {
            throw new ChatNotFoundException(String.format("Chat %d not found", tgChatId));
        }
    }
}
