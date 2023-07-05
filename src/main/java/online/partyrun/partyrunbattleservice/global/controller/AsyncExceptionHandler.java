package online.partyrun.partyrunbattleservice.global.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.warn("비동기 처리중 예외가 발생했습니다\n" +
                "예외 메세지 : " + ex.getMessage() + "\n" +
                "메소드 : " + method.getName() + "\n" +
                "파라미터 : " + Arrays.toString(params));
    }
}
