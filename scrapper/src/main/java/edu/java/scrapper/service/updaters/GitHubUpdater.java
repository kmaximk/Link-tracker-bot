package edu.java.scrapper.service.updaters;

import edu.java.dto.LinkUpdateRequest;
import edu.java.scrapper.clients.botclient.BotClient;
import edu.java.scrapper.clients.github.GitHubClient;
import edu.java.scrapper.clients.github.GitHubResponse;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubUpdater implements Updater {

    private final LinkService linkService;

    private final GitHubClient gitHubClient;

    private final BotClient botClient;

    private final TgChatService tgChatService;

    @Override
    public void handleUpdates(LinkModel link) {
        URI uri = link.url();
        String[] uriParts = uri.getPath().split("/");
        GitHubResponse gitHubResponse = gitHubClient.getRepositoryInfo(uriParts[1], uriParts[2]);
        OffsetDateTime currentTime = OffsetDateTime.now();
        OffsetDateTime newDate = gitHubResponse.updatedAt();
        if (gitHubResponse.updatedAt().isBefore(gitHubResponse.pushedAt())) {
            newDate = gitHubResponse.pushedAt();
        }
        if (gitHubResponse.openIssuesCount().compareTo(link.updatesCount()) != 0) {
            sendUpdate(link, String.format("Number of open github issues has changed for link %s", link.url()));
        } else if (!newDate.isEqual(link.updatedAt())) {
            sendUpdate(link, String.format("New changes in the repository %s", link.url()));
        }
        linkService.updateLink(link, currentTime, newDate, gitHubResponse.openIssuesCount());
    }

    @Override
    public void update(LinkModel link) {
        String[] uriParts = link.url().getPath().split("/");
        GitHubResponse gitHubResponse = gitHubClient.getRepositoryInfo(uriParts[1], uriParts[2]);
        linkService.updateLink(
            link,
            OffsetDateTime.now(),
            gitHubResponse.updatedAt(),
            gitHubResponse.openIssuesCount()
        );
    }

    @Override
    public boolean supports(URI link) {
        return link.getHost().contains("github");
    }

    private void sendUpdate(LinkModel link, String description) {
        botClient.sendUpdates(new LinkUpdateRequest(
            link.id(),
            link.url(),
            description,
            tgChatService.getChatsByLink(link.id())
        ));
    }
}


