package edu.java.scrapper.service.jooq;

import edu.java.scrapper.controller.exceptions.LinkNotFoundException;
import edu.java.scrapper.controller.exceptions.ReAddingLinkException;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.tables.Assignment.ASSIGNMENT;
import static edu.java.scrapper.domain.jooq.tables.Link.LINK;

@RequiredArgsConstructor
public class JooqLinkService implements LinkService {

    private final DSLContext context;

    @Override
    @Transactional
    public LinkModel add(long tgChatId, URI url) {
        LinkModel link = context
            .selectFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOneInto(LinkModel.class);
        if (link == null) {
            link = context.insertInto(LINK)
                .columns(LINK.URL, LINK.LAST_CHECK_TIME, LINK.UPDATED_AT, LINK.UPDATES_COUNT)
                .values(url.toString(), OffsetDateTime.now(), OffsetDateTime.now(), 0)
                .returning().fetchSingleInto(LinkModel.class);
        }
        if (context.fetchCount(ASSIGNMENT, ASSIGNMENT.CHAT_ID.eq(tgChatId)
            .and(ASSIGNMENT.LINK_ID.eq(link.id()))) > 0) {
            throw new ReAddingLinkException(String.format("Link %s is already tracked", link.url()));
        }
        context.insertInto(ASSIGNMENT,
                ASSIGNMENT.LINK_ID, ASSIGNMENT.CHAT_ID
            )
            .values(link.id(), tgChatId)
            .execute();
        return link;
    }

    @Override
    @Transactional
    public LinkModel remove(long tgChatId, URI url) {
        LinkModel link = context.select()
            .from(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOneInto(LinkModel.class);
        if (link == null
            || context.deleteFrom(ASSIGNMENT)
            .where(ASSIGNMENT.LINK_ID.eq(link.id()))
            .execute() == 0) {
            throw new LinkNotFoundException(String.format("Link %s is not tracked", url));
        }
        return link;
    }

    @Override
    @Transactional
    public List<LinkModel> listAll(long tgChatId) {
        return context.select(LINK.fields())
            .from(LINK)
            .join(ASSIGNMENT)
            .on(LINK.ID.eq(ASSIGNMENT.LINK_ID))
            .where(ASSIGNMENT.CHAT_ID.eq(tgChatId))
            .fetchInto(LinkModel.class);
    }

    @Override
    public void updateLink(LinkModel link, OffsetDateTime checkTime, OffsetDateTime updatedAt, Integer updatesCount) {
        context.update(LINK)
            .set(LINK.LAST_CHECK_TIME, checkTime)
            .set(LINK.UPDATED_AT, updatedAt)
            .set(LINK.UPDATES_COUNT, updatesCount)
            .where(LINK.ID.eq(link.id()))
            .execute();
    }

    @Override
    public List<LinkModel> getOutdatedLinks(Long interval) {
        return context.selectFrom(LINK)
            .where(LINK.LAST_CHECK_TIME.le(OffsetDateTime.now().minusSeconds(interval)))
            .fetchInto(LinkModel.class);
    }
}
