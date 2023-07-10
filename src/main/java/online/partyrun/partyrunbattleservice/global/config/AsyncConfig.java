package online.partyrun.partyrunbattleservice.global.config;

import online.partyrun.partyrunbattleservice.global.controller.AsyncExceptionHandler;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private static final int THREAD_COUNT = 2;

    @Override
    public Executor getAsyncExecutor() {
        return Executors.newFixedThreadPool(THREAD_COUNT);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
