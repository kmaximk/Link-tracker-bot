package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends AbstractTextCommand {
    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Remove link from tracked";
    }

    @Override
    public SendMessage handleText(Update update, String message) {
        Repository.remove(update.message().chat().id(), message);
        return new SendMessage(update.message().chat().id(), "Link removed from tracked\n");
    }
}
