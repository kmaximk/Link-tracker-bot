package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Repository;
import edu.java.bot.Utils;
import java.util.Map;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest {
    StartCommand startCommand;

    @Mock
    Update mockUpdate;

    @Mock
    Message mockMessage;

    @Mock
    Chat mockChat;

    @Mock
    Repository mockRepository;

    @BeforeEach
    public void setup() {
        startCommand = new StartCommand(mockRepository);
    }

    @Test
    public void handleUserNotRegisteredTest() {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Map<String, Object> result = startCommand.handle(mockUpdate).getParameters();
        Long resultChatId = (Long) result.get("chat_id");

        assertEquals(5001L, resultChatId);
        verify(mockRepository, times(1)).register(5001L);
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
