package edu.java.scrapper.controller;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LinksController implements LinksApi {

    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> linksDelete(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        log.info("link deleted {}", removeLinkRequest.link());
        return ResponseEntity.status(HttpStatus.OK).body(new LinkResponse(tgChatId, removeLinkRequest.link()));
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> linksGet(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId
    ) {
        return new ResponseEntity<>(
            new ListLinksResponse(
                List.of(new LinkResponse(tgChatId, URI.create("http://localhost:8080"))),
                1
            ),
            HttpStatus.OK
        );
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> linksPost(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) {
        log.info("link added {}", addLinkRequest.link());
        return new ResponseEntity<>(new LinkResponse(tgChatId, addLinkRequest.link()), HttpStatus.OK);
    }
}
