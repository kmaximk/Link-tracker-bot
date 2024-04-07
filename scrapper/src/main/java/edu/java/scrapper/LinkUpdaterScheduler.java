package edu.java.scrapper;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.models.LinkModel;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.updaters.Updater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LinkUpdaterScheduler {

    private final ApplicationConfig config;

    private final LinkService linkService;

    private final List<Updater> updaters;

    @Scheduled(fixedDelayString = "#{@schedulerDelay}")
    public void update() {
        List<LinkModel> links = linkService.getOutdatedLinks(
            config.scheduler().forceCheckDelay().getSeconds()
        );
        log.info("Links updated {}", links);
        for (LinkModel link : links) {
            updaters.forEach(updater -> {
                if (updater.supports(link.url())) {
                    updater.handleUpdates(link);
                }
            });
        }
    }
}
