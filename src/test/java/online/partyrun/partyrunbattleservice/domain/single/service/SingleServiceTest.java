package online.partyrun.partyrunbattleservice.domain.single.service;

import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.single.dto.*;
import online.partyrun.partyrunbattleservice.domain.single.exception.InvalidSingleOwnerException;
import online.partyrun.partyrunbattleservice.domain.single.exception.SingleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.single.repository.SingleRepository;
import online.partyrun.partyrunbattleservice.global.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("SingleService")
class SingleServiceTest extends ServiceTest {

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
                        new RunningTimeRequest(1,1,1),
                        List.of(
                                new SingleRunnerRecordRequest(0, 0, 0, now, 0),
                                new SingleRunnerRecordRequest(0.0001, 0.0001, 0.0001, now, 1)
                        )
                )
        );

        assertThat(response.id()).isNotBlank();
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 싱글_기록을_조회할_때 {
        String singleId;

        @BeforeEach
        void setUp() {
            singleId = singleService.create(
                    runnerId,
                    new SingleRunnerRecordsRequest(
                            new RunningTimeRequest(1,1,1),
                            List.of(
                                    new SingleRunnerRecordRequest(0, 0, 0, now, 0),
                                    new SingleRunnerRecordRequest(0.0001, 0.0001, 0.0001, now, 1)
                            )
                    )
            ).id();
        }

        @Test
        @DisplayName("기록의 주인이 아니라면 예외를 던진다.")
        void throwNotOwnerException() {
            assertThatThrownBy(() -> singleService.getSingle(singleId, "notOwnerId"))
                    .isInstanceOf(InvalidSingleOwnerException.class);
        }

        @Test
        @DisplayName("기록이 존재하지 않으면 예외를 던진다.")
        void throwNotFoundException() {
            assertThatThrownBy(() -> singleService.getSingle("invalid" + singleId, runnerId))
                    .isInstanceOf(SingleNotFoundException.class);
        }

        @Test
        @DisplayName("싱글 기록을 조회한다.")
        void getSingle() {
            final SingleResponse response = singleService.getSingle(singleId, runnerId);
            assertAll(
                    () -> assertThat(response.runningTime()).isEqualTo(new RunningTimeResponse(1, 1, 1)),
                    () -> assertThat(response.records()).hasSize(2)
            );
        }
    }
}
