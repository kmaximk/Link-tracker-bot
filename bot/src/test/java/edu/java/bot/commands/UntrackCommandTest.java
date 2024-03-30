package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Utils;
import edu.java.bot.clients.ScrapperClient;
import edu.java.dto.LinkResponse;
import edu.java.dto.RemoveLinkRequest;
import java.net.URI;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest {
    UntrackCommand untrackCommand;

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
        untrackCommand = new UntrackCommand(scrapperClient);
    }

    @Test
    public void handleUpdateTest() {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        String link = "https://github.com/repo/owner";
        when(scrapperClient
            .deleteLinks(
                5001L,
                new RemoveLinkRequest(URI.create(link))
            ))
            .thenReturn(new LinkResponse(
                5001L,
                URI.create(link)
            ));
        Utils.fillMockText(mockUpdate, mockMessage, "/untrack " + link);
        Map<String, Object> result = untrackCommand.handle(mockUpdate).getParameters();
        Long resultChatId = (Long) result.get("chat_id");
        assertEquals(5001, resultChatId);
        verify(scrapperClient, times(1)).deleteLinks(
            5001L,
            new RemoveLinkRequest(URI.create(link))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"/untrack link1 link2", "/untrack"})
    public void handleWrongLinkTest(String value) {
        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        Utils.fillMockText(mockUpdate, mockMessage, value);
        Map<String, Object> result = untrackCommand.handle(mockUpdate).getParameters();
        Long resultChatId = (Long) result.get("chat_id");
        assertEquals(5001, resultChatId);
        verifyNoInteractions(scrapperClient);
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
