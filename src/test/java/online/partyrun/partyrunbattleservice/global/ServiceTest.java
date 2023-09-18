package online.partyrun.partyrunbattleservice.global;

import online.partyrun.partyrunbattleservice.domain.battle.config.TestApplicationContextConfig;
import online.partyrun.partyrunbattleservice.domain.battle.config.TestTimeConfig;
import online.partyrun.testmanager.redis.EnableRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@EnableRedisTest
@Import({TestApplicationContextConfig.class, TestTimeConfig.class})
public abstract class ServiceTest {
}
