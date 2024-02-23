package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {

    private final Repository repository;

    @Autowired
    public StartCommand(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Runs bot";
    }

    @Override
    public SendMessage handle(Update update) {
        repository.register(update.message().chat().id());
        return new SendMessage(update.message().chat().id(), "Registered user");
    }
}
