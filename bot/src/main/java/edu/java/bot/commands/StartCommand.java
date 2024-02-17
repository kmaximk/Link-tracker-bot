package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Repository;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {

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
        Repository.register(update.message().chat().id());
        return new SendMessage(update.message().chat().id(), "Registered user");
    }
}
