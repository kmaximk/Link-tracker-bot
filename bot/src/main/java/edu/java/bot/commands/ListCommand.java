package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ListCommand implements Command {
    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Shows the list of tracked links";
    }

    @Override
    public SendMessage handle(Update update) {
        List<String> links = Repository.getLinks(update.message().chat().id());
        if (links == null) {
            return new SendMessage(update.message().chat().id(), "You are not registered do /start\n");
        } else if (links.isEmpty()) {
            return new SendMessage(update.message().chat().id(), "No links present, add link /track\n");
        }
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < links.size(); i++) {
            message.append(String.format("%d) %s\n", i + 1, links.get(i)));
        }
        return new SendMessage(update.message().chat().id(), message.toString());
    }
}
