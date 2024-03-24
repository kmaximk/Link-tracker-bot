package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.controller.exceptions.ChatNotFoundException;
import edu.java.scrapper.controller.exceptions.ReRegistrationException;
import edu.java.scrapper.domain.AssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.service.TgChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {

    private final JdbcChatRepository chatRepository;

    private final AssignmentRepository assignmentRepository;

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

    @Override
    public List<Long> getChatsByLink(Long linkID) {
        return assignmentRepository.findChatsByLink(linkID);
    }
}
