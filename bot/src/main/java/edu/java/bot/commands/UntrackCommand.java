package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.clients.ScrapperResponseException;
import edu.java.dto.LinkResponse;
import edu.java.dto.RemoveLinkRequest;
import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends AbstractTextCommand {
    public UntrackCommand(ScrapperClient scrapperClient) {
        super(scrapperClient);
    }

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Removes link from tracked";
    }

    @Override
    public SendMessage handleText(Update update, String message) {
        try {
            LinkResponse linkResponse = scrapperClient.deleteLinks(
                update.message().chat().id(),
                new RemoveLinkRequest(URI.create(message))
            );
            return new SendMessage(
                update.message().chat().id(),
                String.format(
                    "Link removed from tracked %s\n",
                    linkResponse.url().toString()
                )
            );
        } catch (ScrapperResponseException e) {
            return new SendMessage(update.message().chat().id(), e.getApiErrorResponse().description());
        }
    }
}
