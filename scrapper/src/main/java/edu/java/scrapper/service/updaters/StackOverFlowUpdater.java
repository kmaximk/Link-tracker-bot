package edu.java.scrapper.service.updaters;

import edu.java.dto.LinkUpdateRequest;
import edu.java.scrapper.clients.botclient.BotClient;
import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import edu.java.scrapper.clients.stackoverflow.StackOverflowResponse;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.scrapper.service.sender.ScrapperQueueProducer;
import edu.java.scrapper.service.sender.UpdateSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackOverFlowUpdater implements Updater {

    private final LinkService linkService;

    private final StackOverflowClient stackOverflowClient;

    private final TgChatService tgChatService;

    private final UpdateSender updateSender;

    @Override
    public void handleUpdates(LinkModel link) {
        URI uri = link.url();
        String[] uriParts = uri.getPath().split("/");
        StackOverflowResponse stackOverflowResponse = stackOverflowClient.getQuestionUpdate(uriParts[2]);
        OffsetDateTime currentTime = OffsetDateTime.now();
        if (!stackOverflowResponse.lastActivityDate().isEqual(link.updatedAt())) {
            if (stackOverflowResponse.answerCount().compareTo(link.updatesCount()) > 0) {
                sendUpdate(link, String.format("New answer added for link %s", link.url()));
            } else {
                sendUpdate(link, String.format("Link %s updated", link.url()));
            }
        }
        linkService.updateLink(link, currentTime,
            stackOverflowResponse.lastActivityDate(), stackOverflowResponse.answerCount()
        );
    }

    @Override
    public void update(LinkModel link) {
        String[] uriParts = link.url().getPath().split("/");
        StackOverflowResponse stackOverflowResponse = stackOverflowClient.getQuestionUpdate(uriParts[2]);
        linkService.updateLink(link, OffsetDateTime.now(),
            stackOverflowResponse.lastActivityDate(), stackOverflowResponse.answerCount()
        );
    }

    @Override
    public boolean supports(URI link) {
        return link.getHost().contains("stackoverflow");
    }

    private void sendUpdate(LinkModel link, String description) {
        List<Long> chats = tgChatService.getChatsByLink(link.id());
        if (!chats.isEmpty()) {
            updateSender.sendUpdates(new LinkUpdateRequest(
                    link.id(),
                    link.url(),
                    description,
                    chats
                )
            );
        }
    }
}


