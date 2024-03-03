package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import edu.java.bot.processor.UserMessageProcessor;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bot implements AutoCloseable, UpdatesListener {

    private final TelegramBot bot;

    private final UserMessageProcessor processor;

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> bot.execute(processor.process(update)));
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void setCommands(List<? extends Command> commands) {
        BotCommand[] botCommands = commands.stream().map(Command::toApiCommand).toList().toArray(new BotCommand[0]);
        bot.execute(new SetMyCommands(botCommands));
    }

    @PostConstruct
    private void start() {
        bot.setUpdatesListener(this);
        setCommands(processor.commands());
    }

    @Autowired
    public Bot(TelegramBot bot, UserMessageProcessor processor) {
        this.bot = bot;
        this.processor = processor;
    }

    @Override
    public void close() {
        bot.shutdown();
    }

}
