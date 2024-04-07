package edu.java.scrapper.service.sender;

import edu.java.dto.LinkUpdateRequest;

public interface UpdateSender {
    void sendUpdates(LinkUpdateRequest update);
}
