package online.partyrun.partyrunbattleservice.acceptance;

import io.restassured.RestAssured;
import online.partyrun.testmanager.redis.EnableRedisTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;

@EnableRedisTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @LocalServerPort protected int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Autowired private MongoTemplate mongoTemplate;

    @AfterEach
    public void tearDown() {
        mongoTemplate.getDb().drop();
    }
}
