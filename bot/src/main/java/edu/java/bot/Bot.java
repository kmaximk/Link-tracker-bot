package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.model.Command;
import edu.java.bot.processor.UserMessageProcessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Bot implements AutoCloseable, UpdatesListener {

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            SendResponse sendResponse = bot.execute(processor.process(update));
            Message message = sendResponse.message();
            System.out.println(message.text());
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void close() {
        bot.shutdown();
    }


    @PostConstruct
    private void start() {
        bot.setUpdatesListener(this);
        processor.commands();

    }

    public final TelegramBot bot;

    public final UserMessageProcessor processor;

    @Autowired
    public Bot(TelegramBot bot, UserMessageProcessor processor) {
        this.bot = bot;
        this.processor = processor;
    }
}
