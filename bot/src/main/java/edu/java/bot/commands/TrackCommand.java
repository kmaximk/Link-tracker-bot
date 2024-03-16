package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import edu.java.bot.clients.ScrapperClient;
import edu.java.dto.AddLinkRequest;
import org.springframework.stereotype.Component;
import java.net.URI;

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
    public SendMessage handleText(Update update, String message) {
        scrapperClient.postLinks(
            update.message().chat().id(),
            new AddLinkRequest(URI.create(message))
        );
        return new SendMessage(update.message().chat().id(), "Link added to tracked\n");
    }
}
