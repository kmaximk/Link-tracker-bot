package edu.java.scrapper.controller;

import edu.java.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface TgChatApi {
    @Operation(
        operationId = "tgChatIdDelete",
        summary = "Удалить чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Чат не существует", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    ResponseEntity<Void> tgChatIdDelete(Long id);

    @Operation(
        operationId = "tgChatIdPost",
        summary = "Зарегистрировать чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class)),
            }),
            @ApiResponse(responseCode = "409", description = "Повторная регистрация чата", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    ResponseEntity<Void> tgChatIdPost(Long id);
}
