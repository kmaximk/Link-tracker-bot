package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTextCommand implements Command {
    private final String spacesRegex = "\\s+";

    protected final ScrapperClient scrapperClient;

    private static final int LINK_MIN_LENGTH = 3;

    private static final List<String> KNOWN_HOSTS = List.of(
        "github.com",
        "stackoverflow.com"
    );

    @Autowired
    public AbstractTextCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    private SendMessage checkLink(URI uri, Update update) {
        Long chatID = update.message().chat().id();
        if (!KNOWN_HOSTS.contains(uri.getHost())) {
            return new SendMessage(chatID, "Only supports github and stackoverflow links");
        } else if (uri.getPath().split("/").length < LINK_MIN_LENGTH) {
            return new SendMessage(chatID, "Link too short");
        }
        return handleURI(update, uri);
    }

    @Override
    public SendMessage handle(Update update) {
        String[] text = update.message().text().split(spacesRegex);
        if (text.length != 2) {
            String message = "One link at a time\n";
            if (text.length < 2) {
                message = "No link provided\n";
            }
            return new SendMessage(update.message().chat().id(), message);
        }
        try {
            return checkLink(new URI(text[1]), update);
        } catch (Exception e) {
            return new SendMessage(update.message().chat().id(), "Link is not correct URL");
        }
    }

    public abstract SendMessage handleURI(Update update, URI uri);

    @Override
    public boolean supports(Update update) {
        if (update.message() != null) {
            String[] text = update.message().text().split(spacesRegex, 2);
            return update.message().text() != null
                && text[0].equals(command());
        }
        return false;
    }
}
