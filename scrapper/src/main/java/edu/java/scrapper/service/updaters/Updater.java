package edu.java.scrapper.service.updaters;

import edu.java.scrapper.models.LinkModel;
import java.net.URI;

public interface Updater {
    void handleUpdates(LinkModel link);

    void update(LinkModel link);

    boolean supports(URI link);
}
