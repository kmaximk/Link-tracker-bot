package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import edu.java.bot.clients.ScrapperClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {
    HelpCommand helpCommand;

    @Mock
    Update mockUpdate;

    @Mock
    Message mockMessage;

    @Mock
    Chat mockChat;

    @Mock
    ScrapperClient scrapperClient;

    @BeforeEach
    public void setup() {
        helpCommand = new HelpCommand(new ArrayList<>(List.of(
            new ListCommand(scrapperClient),
            new StartCommand(scrapperClient),
            new TrackCommand(scrapperClient),
            new UntrackCommand(scrapperClient)
        )));
    }

    @Test
    public void handleUpdateTest() {

        List<String> expectedCommands = List.of(
            "/help",
            "/track",
            "/untrack",
            "/list",
            "/start"
        );

        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Map<String, Object> result = helpCommand.handle(mockUpdate).getParameters();

        String textResult = (String) result.get("text");
        Long chatId = (Long) result.get("chat_id");
        List<String> commandsRes = new ArrayList<>();
        for (String command : textResult.lines().toList()) {
            String[] res = command.split("\\s+", 2);
            commandsRes.add(res[0]);
            assertFalse(res[1].isBlank());
        }

        assertEquals(5001, chatId);
        assertEquals(5, commandsRes.size());
        for (String command : expectedCommands) {
            assertTrue(commandsRes.contains(command));
        }
    }

    @Test
    public void supportsTrueTest() {
        Utils.fillMockText(mockUpdate, mockMessage, "/help");
        assertTrue(helpCommand.supports(mockUpdate));
    }

    @ParameterizedTest
    @ValueSource(strings = {"some text", "/help help", "/helphelp"})
    public void supportsFalseTest(String value) {
        Utils.fillMockText(mockUpdate, mockMessage, value);
        assertFalse(helpCommand.supports(mockUpdate));
    }
}
