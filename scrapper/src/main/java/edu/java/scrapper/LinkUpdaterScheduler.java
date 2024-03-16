package edu.java.scrapper;

import edu.java.scrapper.models.Link;
import edu.java.dto.LinkUpdateRequest;
import edu.java.scrapper.clients.botclient.BotClient;
import edu.java.scrapper.clients.github.GitHubClient;
import edu.java.scrapper.clients.github.GitHubResponse;
import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import edu.java.scrapper.clients.stackoverflow.StackOverflowResponse;
import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final JdbcAssignmentRepository assignmentRepository;

    private final GitHubClient gitHubClient;

    private final StackOverflowClient stackOverflowClient;

    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@schedulerDelay}")
    public void update() {
        log.info("Links updated");
        List<Link> links = assignmentRepository.getOutdatedLinks(10L);
        System.out.println(links);
        for (Link link : links) {
            URI uri = link.url();
            System.out.println(uri);;
            String[] parts = uri.getPath().split("/");
            if (uri.getHost().contains("github")) {
                GitHubResponse gitHubResponse = gitHubClient.getRepositoryInfo(parts[1], parts[2]);
                sendUpdate(gitHubResponse.updatedAt(), link);
            } else if (uri.getHost().contains("stackoverflow")) {
                StackOverflowResponse response = stackOverflowClient.getQuestionUpdate(parts[2]);
                sendUpdate(response.lastActivityDate(), link);
            }
        }
    }

    private void sendUpdate(OffsetDateTime responseTime, Link link) {
        if (responseTime != link.updatedAt()) {
            botClient.sendUpdates(new LinkUpdateRequest(
                link.id(),
                link.url(),
                "Links updated",
                assignmentRepository.findChatsByLink(link.id()
                )
            ));
        }
    }
}
