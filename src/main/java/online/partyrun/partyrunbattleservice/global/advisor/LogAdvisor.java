package online.partyrun.partyrunbattleservice.global.advisor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class LogAdvisor {

    @Around("within(online.partyrun.partyrunbattleservice..*)")
    public Object generateTraceLong(ProceedingJoinPoint joinPoint) throws Throwable {
        final String logFormat = String.format("class: %s, method: %s, args: %s",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));

        log.info("[log] REQUEST - {}", logFormat);

        final Object result = joinPoint.proceed();

        log.info("[log] RESPONSE - {}, result = {}", logFormat, result);

        return result;
    }
}
