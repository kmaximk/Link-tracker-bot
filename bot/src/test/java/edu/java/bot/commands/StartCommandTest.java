package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Repository;
import edu.java.bot.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest {
    StartCommand startCommand;

    @Mock
    Update mockUpdate;

    @Mock
    Message mockMessage;

    @Mock
    Chat mockChat;

    @BeforeEach
    public void setup() {
        startCommand = new StartCommand();
    }

    @Test
    public void handleUserNotRegisteredTest() {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        try (MockedStatic<Repository> mocked = mockStatic(Repository.class)) {
            Map<String, Object> result = startCommand.handle(mockUpdate).getParameters();
            Long resultChatId = (Long) result.get("chat_id");
            assertEquals(5001, resultChatId);
            mocked.verify(() -> Repository.register(5001L), times(1));
        }
    }

    @Test
    public void supportsTrueTest() {
        Utils.fillMockText(mockUpdate, mockMessage, "/start");
        assertTrue(startCommand.supports(mockUpdate));
    }

    @ParameterizedTest
    @ValueSource(strings = {"some text", "/start/start"})
    public void supportsFalseTest(String value) {
        Utils.fillMockText(mockUpdate, mockMessage, value);
        assertFalse(startCommand.supports(mockUpdate));
    }
}
