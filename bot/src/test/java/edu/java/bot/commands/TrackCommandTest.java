package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Repository;
import java.util.Map;
import edu.java.bot.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest {
    TrackCommand trackCommand;

    @Mock
    Update mockUpdate;

    @Mock
    Message mockMessage;

    @Mock
    Chat mockChat;

    @BeforeEach
    public void setup() {
        trackCommand = new TrackCommand();
    }

    @Test
    public void handleUpdateTest() {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, "/track https://github.com/");
        try (MockedStatic<Repository> mocked = mockStatic(Repository.class)) {
            Map<String, Object> result = trackCommand.handle(mockUpdate).getParameters();
            Long resultChatId = (Long) result.get("chat_id");
            assertEquals(5001, resultChatId);
            mocked.verify(() -> Repository.put(5001L, "https://github.com/"), times(1));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/track link1 link2", "/track"})
    public void handleWrongLinkTest(String value) {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, value);
        try (MockedStatic<Repository> mocked = mockStatic(Repository.class)) {
            Map<String, Object> result = trackCommand.handle(mockUpdate).getParameters();
            Long resultChatId = (Long) result.get("chat_id");
            assertEquals(5001, resultChatId);
            mocked.verifyNoInteractions();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/track", "/track some link", "/track some"})
    public void supportsTrueTest(String value) {
        Utils.fillMockText(mockUpdate, mockMessage, value);
        assertTrue(trackCommand.supports(mockUpdate));
    }

    @ParameterizedTest
    @ValueSource(strings = {"some text", "/track/track"})
    public void supportsFalseTest(String value) {
        Utils.fillMockText(mockUpdate, mockMessage, value);
        assertFalse(trackCommand.supports(mockUpdate));
    }
}
