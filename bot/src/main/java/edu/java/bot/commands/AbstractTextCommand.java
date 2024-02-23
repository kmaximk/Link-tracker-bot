package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTextCommand implements Command {
    private final String spacesRegex = "\\s+";

    protected final Repository repository;

    @Autowired
    public AbstractTextCommand(Repository repository) {
        this.repository = repository;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] text = update.message().text().split(spacesRegex);
        if (text.length > 2) {
            return new SendMessage(update.message().chat().id(), "One link at a time\n");
        } else if (text.length < 2) {
            return new SendMessage(update.message().chat().id(), "Link not specified\n");
        }
        return handleText(update, text[1]);
    }

    public abstract SendMessage handleText(Update update, String message);

    @Override
    public boolean supports(Update update) {
        String[] text = update.message().text().split(spacesRegex, 2);
        return update.message() != null && update.message().text() != null
            && text[0].equals(command());
    }
}
