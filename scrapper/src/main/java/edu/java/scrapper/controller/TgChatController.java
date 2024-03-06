package edu.java.scrapper.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TgChatController implements TgChatApi {

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> tgChatIdDelete(@PathVariable("id") Long id) {
        log.info("Tg chat id deleted {}", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> tgChatIdPost(@PathVariable("id") Long id) {
        log.info("Tg chat id registered {}", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
