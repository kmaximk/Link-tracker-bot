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
public class UntrackCommandTest {
    UntrackCommand untrackCommand;

    @Mock
    Update mockUpdate;

    @Mock
    Message mockMessage;

    @Mock
    Chat mockChat;

    @BeforeEach
    public void setup() {
        untrackCommand = new UntrackCommand();
    }

    @Test
    public void handleUpdateTest() {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, "/untrack https://github.com/");
        try (MockedStatic<Repository> mocked = mockStatic(Repository.class)) {
            Map<String, Object> result = untrackCommand.handle(mockUpdate).getParameters();
            Long resultChatId = (Long) result.get("chat_id");
            assertEquals(5001, resultChatId);
            mocked.verify(() -> Repository.remove(5001L, "https://github.com/"), times(1));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/untrack link1 link2", "/untrack"})
    public void handleWrongLinkTest(String value) {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, value);
        try (MockedStatic<Repository> mocked = mockStatic(Repository.class)) {
            Map<String, Object> result = untrackCommand.handle(mockUpdate).getParameters();
            Long resultChatId = (Long) result.get("chat_id");
            assertEquals(5001, resultChatId);
            mocked.verifyNoInteractions();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/untrack", "/untrack some link", "/untrack some"})
    public void supportsTrueTest(String value) {
        Utils.fillMockText(mockUpdate, mockMessage, value);
        assertTrue(untrackCommand.supports(mockUpdate));
    }

    @ParameterizedTest
    @ValueSource(strings = {"some text", "/untrack/untrack"})
    public void supportsFalseTest(String value) {
        Utils.fillMockText(mockUpdate, mockMessage, value);
        assertFalse(untrackCommand.supports(mockUpdate));
    }
}
