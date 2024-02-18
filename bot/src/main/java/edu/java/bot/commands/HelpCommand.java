package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    private final List<Command> commands;

    @Override
    public String command() {
        return "/help";
    }

    @Autowired
    public HelpCommand(List<Command> commands) {
        this.commands = commands;
        commands.add(this);
    }

    @Override
    public String description() {
        return "Shows available commands";
    }

    @Override
    public SendMessage handle(Update update) {
        StringBuilder message = new StringBuilder();
        for (Command command : commands) {
            message.append(String.format("%s %s\n", command.command(), command.description()));
        }
        return new SendMessage(update.message().chat().id(), message.toString());
    }
}
