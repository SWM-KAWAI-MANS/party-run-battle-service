package online.partyrun.partyrunbattleservice.acceptance;

import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort private int port;

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
