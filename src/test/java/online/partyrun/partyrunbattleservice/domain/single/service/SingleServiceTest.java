package online.partyrun.partyrunbattleservice.domain.single.service;

import online.partyrun.partyrunbattleservice.domain.single.dto.SingleIdResponse;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleRunnerRecordRequest;
import online.partyrun.partyrunbattleservice.domain.single.dto.SingleRunnerRecordsRequest;
import online.partyrun.partyrunbattleservice.domain.single.repository.SingleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("SingleService")
class SingleServiceTest {

    @Autowired
    SingleService singleService;
    @Autowired
    SingleRepository singleRepository;
    String runnerId = "박성우";
    LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("싱글 기록을 생성할 수 있다.")
    void create() {
        final SingleIdResponse response = singleService.create(
                runnerId,
                new SingleRunnerRecordsRequest(
                        List.of(
                                new SingleRunnerRecordRequest(0, 0, 0, now, 0),
                                new SingleRunnerRecordRequest(0.0001, 0.0001, 0.0001, now, 1)
                        )
                )
        );

        assertThat(response.id()).isNotBlank();
    }
}
