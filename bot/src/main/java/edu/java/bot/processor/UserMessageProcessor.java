package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserMessageProcessor {

    private final List<? extends Command> commands;

    @Autowired
    public UserMessageProcessor(List<? extends Command> commands) {
        this.commands = commands;
    }

    public List<? extends Command> commands() {
        return commands;
    }

    public SendMessage process(Update update) {
        for (Command command : commands) {
            if (command.supports(update)) {
                return command.handle(update);
            }
        }
        if (update.message() != null) {
            return new SendMessage(
                update.message().chat().id(),
                "Unsupported command. Use /help to get list of commands"
            );
        }
        return null;
    }
}
