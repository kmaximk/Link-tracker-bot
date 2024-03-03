package edu.java.scrapper.controller;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface LinksApi {
    @Operation(
        operationId = "linksDelete",
        summary = "Убрать отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LinkResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Ссылка не найдена", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    ResponseEntity<LinkResponse> linksDelete(Long tgChatId, RemoveLinkRequest removeLinkRequest);

    @Operation(
        operationId = "linksGet",
        summary = "Получить все отслеживаемые ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылки успешно получены", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ListLinksResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    ResponseEntity<ListLinksResponse> linksGet(Long tgChatId);

    @Operation(
        operationId = "linksPost",
        summary = "Добавить отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LinkResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "409", description = "Повторное добавление ссылки", content = {
                @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    ResponseEntity<LinkResponse> linksPost(Long tgChatId, AddLinkRequest addLinkRequest);
}
