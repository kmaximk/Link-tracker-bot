package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Utils;
import java.net.URI;
import java.util.Map;
import edu.java.bot.clients.ScrapperClient;
import edu.java.dto.AddLinkRequest;
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
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest {
    TrackCommand trackCommand;

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
        trackCommand = new TrackCommand(scrapperClient);
    }

    @Test
    public void handleUpdateTest() {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, "/track https://github.com/");
        Map<String, Object> result = trackCommand.handle(mockUpdate).getParameters();
        Long resultChatId = (Long) result.get("chat_id");

        assertEquals(5001, resultChatId);
        verify(scrapperClient, times(1)).postLinks(5001L,
                new AddLinkRequest(URI.create("https://github.com/")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/track link1 link2", "/track"})
    public void handleWrongLinkTest(String value) {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, value);
        Map<String, Object> result = trackCommand.handle(mockUpdate).getParameters();
        Long resultChatId = (Long) result.get("chat_id");

        assertEquals(5001, resultChatId);
        verifyNoInteractions(scrapperClient);
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
