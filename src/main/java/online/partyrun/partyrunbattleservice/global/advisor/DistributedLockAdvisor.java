package online.partyrun.partyrunbattleservice.global.advisor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import online.partyrun.partyrunbattleservice.global.annotation.DistributedLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DistributedLockAdvisor {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    RedissonClient redissonClient;

    @Around("@annotation(online.partyrun.partyrunbattleservice.global.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key =
                REDISSON_LOCK_PREFIX
                        + CustomSpelParser.getDynamicValue(
                                signature.getParameterNames(),
                                joinPoint.getArgs(),
                                distributedLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available =
                    rLock.tryLock(
                            distributedLock.waitTime(),
                            distributedLock.leaseTime(),
                            distributedLock.timeUnit());

            if (!available) {
                return false;
            }

            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            unLock(rLock, key, method);
        }
    }

    private void unLock(RLock rLock, String key, Method method) {
        try {
            rLock.unlock();
        } catch (IllegalMonitorStateException e) {
            log.info(
                    String.format(
                            "%s 메소드에 걸린 %s 키 Reddison Lock은 이미 unLock 상태입니다.",
                            method.getName(), key));
        }
    }
}
