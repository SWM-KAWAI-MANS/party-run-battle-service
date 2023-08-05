package online.partyrun.partyrunbattleservice;

import online.partyrun.partyrunbattleservice.domain.battle.config.EmbeddedRedisCallBack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(EmbeddedRedisCallBack.class)
class PartyRunBattleServiceApplicationTests {

    @Test
    void contextLoads() {}
}
