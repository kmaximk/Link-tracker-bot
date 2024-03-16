package edu.java.scrapper.controller;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.service.LinkService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LinksController implements LinksApi {

    private final LinkService linkService;

    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> linksDelete(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    ) {

        log.info("link deleted {}", removeLinkRequest.link());
        Link link = linkService.remove(tgChatId, removeLinkRequest.link());
        return ResponseEntity.status(HttpStatus.OK).body(new LinkResponse(tgChatId, link.url()));
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> linksGet(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId
    ) {
        List<LinkResponse> response = linkService.listAll(tgChatId).stream().map(
            link -> new LinkResponse(tgChatId, link.url())).toList();
        return new ResponseEntity<>(
            new ListLinksResponse(response, response.size()),
            HttpStatus.OK
        );
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> linksPost(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) {
        log.info("link added {}", addLinkRequest.link());
        Link link = linkService.add(tgChatId, addLinkRequest.link());
        return new ResponseEntity<>(new LinkResponse(tgChatId, link.url()), HttpStatus.OK);
    }
}
