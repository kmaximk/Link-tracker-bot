package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends AbstractTextCommand {
    public UntrackCommand(Repository repository) {
        super(repository);
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
        boolean ok = repository.remove(update.message().chat().id(), message);
        if (!ok) {
            return new SendMessage(update.message().chat().id(), "Link not found\n");
        }
        return new SendMessage(update.message().chat().id(), "Link removed from tracked\n");
    }
}
