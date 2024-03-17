package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.clients.ScrapperResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartCommand implements Command {

    private final ScrapperClient scrapperClient;

    @Autowired
    public StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
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
        try {
            scrapperClient.registerTgChat(update.message().chat().id());
        } catch (ScrapperResponseException e) {
            log.error("error in start command", e);
            return new SendMessage(update.message().chat().id(), e.getApiErrorResponse().description());
        }
        return new SendMessage(update.message().chat().id(), "Registered user");
    }
}
