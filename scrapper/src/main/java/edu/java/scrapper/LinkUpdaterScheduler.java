package edu.java.scrapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LinkUpdaterScheduler {

    @Scheduled(fixedDelayString = "#{@schedulerDelay}")
    public void update() {
        log.info("Links updated");
    }
}
