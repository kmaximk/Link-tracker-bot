package edu.java.bot.controller;

import edu.java.dto.ApiErrorResponse;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
    basePackageClasses = {UpdatesController.class},
    basePackages = "edu.java.bot.controller")
@Slf4j
public class UpdatesExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ApiErrorResponse> handleNullPointerException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException", ex);
        List<String> stackTraceElements = Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorResponse("Некорректные параметры запроса",
                ex.getStatusCode().toString(),
                ex.getClass().getName(),
                ex.getMessage(),
                stackTraceElements
            ));
    }
}
