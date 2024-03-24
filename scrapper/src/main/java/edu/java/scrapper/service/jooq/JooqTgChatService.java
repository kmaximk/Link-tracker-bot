package edu.java.scrapper.service.jooq;

import edu.java.scrapper.controller.exceptions.ChatNotFoundException;
import edu.java.scrapper.controller.exceptions.ReRegistrationException;
import edu.java.scrapper.domain.jooq.tables.Assignment;
import edu.java.scrapper.domain.jooq.tables.Chat;
import edu.java.scrapper.service.TgChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class JooqTgChatService implements TgChatService {

    private final DSLContext context;

    @Override
    public void register(long tgChatId) {
        if (context.fetchCount(Chat.CHAT, Chat.CHAT.ID.eq(tgChatId)) > 0) {
            throw new ReRegistrationException(String.format("Chat %d already exists", tgChatId));
        }
        context.insertInto(Chat.CHAT)
            .columns(Chat.CHAT.ID)
            .values(tgChatId)
            .execute();
    }

    @Override
    public void unregister(long tgChatId) {
        int deleted = context.deleteFrom(Chat.CHAT)
            .where(Chat.CHAT.ID.eq(tgChatId))
            .execute();
        if (deleted == 0) {
            throw new ChatNotFoundException(String.format("Chat %d not found", tgChatId));
        }
    }

    @Override
    public List<Long> getChatsByLink(Long linkID) {
        return context.select(Chat.CHAT.fields())
            .from(Chat.CHAT)
            .join(Assignment.ASSIGNMENT)
            .on(Chat.CHAT.ID.eq(Assignment.ASSIGNMENT.CHAT_ID))
            .where(Assignment.ASSIGNMENT.LINK_ID.eq(linkID))
            .fetchInto(Long.class);
    }
}
