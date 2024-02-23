package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand extends AbstractTextCommand {
    public TrackCommand(Repository repository) {
        super(repository);
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
        repository.put(update.message().chat().id(), message);
        return new SendMessage(update.message().chat().id(), "Link added to tracked\n");
    }
}
