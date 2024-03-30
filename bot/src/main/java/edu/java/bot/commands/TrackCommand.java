package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.dto.AddLinkRequest;
import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand extends AbstractTextCommand {
    public TrackCommand(ScrapperClient scrapperClient) {
        super(scrapperClient);
    }

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "Adds link to tracked";
    }

    @Override
    public SendMessage handleURI(Update update, URI uri) {
        scrapperClient.postLinks(
            update.message().chat().id(),
            new AddLinkRequest(uri)
        );
        return new SendMessage(update.message().chat().id(), "Link added to tracked\n");
    }
}
