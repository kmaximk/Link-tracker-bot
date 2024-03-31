package edu.java.scrapper.controller;

import edu.java.dto.ApiErrorResponse;
import edu.java.scrapper.controller.exceptions.ChatNotFoundException;
import edu.java.scrapper.controller.exceptions.LinkNotFoundException;
import edu.java.scrapper.controller.exceptions.ReAddingLinkException;
import edu.java.scrapper.controller.exceptions.ReRegistrationException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;

@RestControllerAdvice(
    basePackageClasses = {LinksController.class, TgChatController.class},
    basePackages = {"edu.java.scrapper.controller"})
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ApiErrorResponse> handleNotValidException(MethodArgumentNotValidException ex) {
        return formResponse(ex, "Request arguments are not valid", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {LinkNotFoundException.class})
    protected ResponseEntity<ApiErrorResponse> handleLinksNotFound(LinkNotFoundException ex) {
        return formResponse(ex, "Link not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ChatNotFoundException.class})
    protected ResponseEntity<ApiErrorResponse> handleChatNotFound(ChatNotFoundException ex) {
        return formResponse(ex, "Chat not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ReAddingLinkException.class})
    protected ResponseEntity<ApiErrorResponse> handleReAdding(ReAddingLinkException ex) {
        return formResponse(ex, "Re-adding link", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {ReRegistrationException.class})
    protected ResponseEntity<ApiErrorResponse> handleReRegistration(ReRegistrationException ex) {
        return formResponse(ex, "Re-registration chat", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {WebClientException.class})
    protected ResponseEntity<ApiErrorResponse> handleException(WebClientException ex) {
        return formResponse(ex, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiErrorResponse> formResponse(Throwable ex, String description, HttpStatus code) {
        log.error(description, ex);
        List<String> stackTraceElements = Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList();
        return ResponseEntity.status(code).body(
            new ApiErrorResponse(description, code.value(),
                ex.getClass().getName(), ex.getMessage(), stackTraceElements
            ));
    }
}
