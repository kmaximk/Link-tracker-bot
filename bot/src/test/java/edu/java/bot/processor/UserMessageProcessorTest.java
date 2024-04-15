package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Utils;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserMessageProcessorTest {

    UserMessageProcessor messageProcessor;

    @Mock
    Update mockUpdate;

    @Mock
    Message mockMessage;

    @Mock
    Chat mockChat;

    @Mock
    ScrapperClient scrapperClient;

    @Autowired MeterRegistry meterRegistry;

    @BeforeEach
    public void setup() {
        messageProcessor = new UserMessageProcessor(new ArrayList<>(List.of(
            new ListCommand(scrapperClient),
            new StartCommand(scrapperClient),
            new TrackCommand(scrapperClient),
            new UntrackCommand(scrapperClient),
            new HelpCommand(new ArrayList<>(List.of(
                new ListCommand(scrapperClient),
                new StartCommand(scrapperClient),
                new TrackCommand(scrapperClient),
                new UntrackCommand(scrapperClient)
            )))
        )), meterRegistry);
    }

    @Test
    public void wrongInputTest() {
        Utils.fillMockText(mockUpdate, mockMessage, "some text");
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Map<String, Object> result = messageProcessor.process(mockUpdate).getParameters();

        assertEquals(5001L, (Long) result.get("chat_id"));
        assertFalse(((String) result.get("text")).isBlank());
    }
}
