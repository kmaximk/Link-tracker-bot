package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.Utils;
import java.net.URI;
import java.util.List;
import java.util.Map;
import edu.java.bot.clients.ScrapperClient;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest {
    ListCommand listCommand;

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
        listCommand = new ListCommand(scrapperClient);
    }

    @Test
    public void handleNormalLinks() {

        List<String> expectedLinks = List.of("link1", "link2", "link3");

        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        when(scrapperClient.getLinks(5001L)).thenReturn(
            new ListLinksResponse(
                expectedLinks.stream().map(link ->
                    new LinkResponse(
                        5001L,
                        URI.create(link)
                    )).toList(),
                expectedLinks.size()
            )
        );

        Map<String, Object> result = listCommand.handle(mockUpdate).getParameters();
        List<String> textResult = ((String) result.get("text")).lines().toList();
        Long resultChatId = (Long) result.get("chat_id");

        assertEquals(5001, resultChatId);
        for (int i = 0; i < textResult.size(); i++) {
            String[] actualText = textResult.get(i).split("\\s+", 2);
            assertEquals(expectedLinks.get(i), actualText[1]);
            assertEquals(String.format("%d)", i + 1), actualText[0]);
        }
    }

    @Test
    public void handleNoLinksTest() {

        Utils.fillMockChatId(mockUpdate, mockMessage, mockChat, 5001L);
        when(scrapperClient.getLinks(5001L)).thenReturn(
            new ListLinksResponse(emptyList(), 0)
        );
        Map<String, Object> result = listCommand.handle(mockUpdate).getParameters();
        List<String> textResult = ((String) result.get("text")).lines().toList();
        Long resultChatId = (Long) result.get("chat_id");

        assertEquals(5001, resultChatId);
        assertEquals(1, textResult.size());
        assertFalse(textResult.getFirst().isBlank());
    }

    @Test
    public void supportsTrueTest() {
        Utils.fillMockText(mockUpdate, mockMessage, "/list");
        assertTrue(listCommand.supports(mockUpdate));
    }

    @ParameterizedTest
    @ValueSource(strings = {"some text", "/list /list", "/listlist"})
    public void supportsFalseTest(String value) {
        Utils.fillMockText(mockUpdate, mockMessage, value);
        assertFalse(listCommand.supports(mockUpdate));
    }

}
