package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.processor.UserMessageProcessor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BotTest {

    @Mock
    Update mockUpdate;

    @Mock
    Message mockMessage;

    @Mock
    Chat mockChat;

    Bot myBot;

    @BeforeEach
    public void setup() {
        myBot = new Bot(new TelegramBot("sometoken"), new UserMessageProcessor(
            new ArrayList<>(List.of(
                new ListCommand(),
                new StartCommand(),
                new TrackCommand(),
                new UntrackCommand()
            ))
        ));
    }

    @Test
    public void botProcessTest() {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, "/start");
        int result = myBot.process(List.of(mockUpdate));
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }
}
