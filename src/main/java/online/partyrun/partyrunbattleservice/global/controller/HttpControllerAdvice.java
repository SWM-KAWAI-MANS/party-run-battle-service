package online.partyrun.partyrunbattleservice.global.controller;

import lombok.extern.slf4j.Slf4j;
import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class HttpControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(BadRequestException exception) {
        log.warn(exception.getMessage());
        return new ExceptionResponse("잘못된 요청입니다.");
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleInternalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return new ExceptionResponse("알 수 없는 에러입니다.");
    }

    @ExceptionHandler({BindException.class, MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBindException(BindException exception) {
        log.error(exception.getMessage());
        return new ExceptionResponse("질못된 요청입니다");
    }
}
