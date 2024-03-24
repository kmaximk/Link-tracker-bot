package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperResponseException;
import edu.java.bot.commands.Command;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                try {
                    return command.handle(update);
                } catch (ScrapperResponseException e) {
                    return new SendMessage(
                        update.message().chat().id(),
                        String.format("Bad answer from server with code %s message %s",
                            e.getApiErrorResponse().code(), e.getApiErrorResponse().exceptionMessage()
                        )
                    );
                }
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
