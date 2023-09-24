package online.partyrun.partyrunbattleservice.domain.battle.controller;

import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;
import online.partyrun.partyrunbattleservice.global.exception.NotFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class BattleMessageControllerAdvice {
    private static final String RIGHT_ARROW = " -> ";
    private static final String EXCEPTION_MESSAGE = "[EXCEPTION]";

    @MessageExceptionHandler({
            BadRequestException.class,
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class
    })
    public void handleBadRequestException(Exception exception) {
        log.warn("{} {}", EXCEPTION_MESSAGE, exception.getMessage());
    }

    @MessageExceptionHandler(BindException.class)
    public void handleBindException(BindException exception) {
        final String message =
                exception.getBindingResult().getAllErrors().stream()
                        .map(error -> String.format("%s: %s", ((FieldError) error).getField(), error.getDefaultMessage()))
                        .collect(Collectors.joining(", "));

        log.warn("{} {}",EXCEPTION_MESSAGE, message);
    }

    @MessageExceptionHandler(NotFoundException.class)
    public void handleNotFoundException(NotFoundException exception) {
        log.warn("{} {}",EXCEPTION_MESSAGE, exception.getMessage());
    }

    @MessageExceptionHandler({RuntimeException.class, Exception.class})
    public void handleInternalServerErrorException(Exception exception) {
        log.error("{} {} \n {}", EXCEPTION_MESSAGE, exception.getMessage(), makeStackTraceMessage(exception));
    }

    private String makeStackTraceMessage(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining(RIGHT_ARROW));
    }
}
