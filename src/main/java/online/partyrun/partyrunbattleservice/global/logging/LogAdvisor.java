package online.partyrun.partyrunbattleservice.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class LogAdvisor {

    @Around("@within(Logging) || @annotation(Logging)")
    public Object logging(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String requestArgs = Arrays.stream(args)
                .filter(arg -> !(arg instanceof Authentication))
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String memberId = getMemberId(args);

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[LOG] REQUEST - class: {}, method: {}, memberId: {}, args: {}", className, methodName, memberId, requestArgs);

        Object result = joinPoint.proceed();

        log.info("[LOG] RESPONSE - class: {}, method: {}, memberId: {}, result: {}", className, methodName, memberId, result);

        return result;
    }

    private String getMemberId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Authentication auth) {
                return auth.getName();
            }
        }
        return null;
    }
}
