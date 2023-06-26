package online.partyrun.partyrunbattleservice.global.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HttpControllerAdvice {

    static String BAD_REQUEST_MESSAGE = "잘못된 요청입니다.";
    static String SERVER_ERROR_MESSAGE = "알 수 없는 에러입니다.";

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(BadRequestException exception) {
        log.warn(exception.getMessage());
        return new ExceptionResponse(BAD_REQUEST_MESSAGE);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBindException(BindException exception) {
        final String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> String.format("%s: %s", ((FieldError) error).getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        log.warn(message);
        return new ExceptionResponse(BAD_REQUEST_MESSAGE);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleInternalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return new ExceptionResponse(SERVER_ERROR_MESSAGE);
    }
}
