package edu.java.bot.model;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public abstract class AbstractTextCommand implements Command{
    @Override
    public SendMessage handle(Update update) {
        String[] text = update.message().text().split("\\s+");
        if (text.length > 2) {
            return new SendMessage(update.message().chat().id(), "One link at a time\n");
        } else if (text.length < 2) {
            return new SendMessage(update.message().chat().id(), "Link not specified\n");
        }
        return handleText(update, text[1]);
    }

    public abstract SendMessage handleText(Update update, String message);
}
