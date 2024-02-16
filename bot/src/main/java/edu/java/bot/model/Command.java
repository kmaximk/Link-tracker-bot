package edu.java.bot.model;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;

public interface Command {
    String command();

    String description();

    SendMessage handle(Update update);

    default boolean supports(Update update) {
        return update.message() != null && update.message().text() != null &&
            update.message().text().startsWith(this.command());
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }
}
