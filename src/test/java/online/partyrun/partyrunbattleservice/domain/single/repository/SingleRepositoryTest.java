package online.partyrun.partyrunbattleservice.domain.single.repository;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.GpsData;
import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import online.partyrun.partyrunbattleservice.domain.single.entity.RunningTime;
import online.partyrun.partyrunbattleservice.domain.single.entity.Single;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("SingleRepository")
class SingleRepositoryTest {

    @Autowired
    SingleRepository singleRepository;

    String runnerId = "박성우";
    LocalDateTime now = LocalDateTime.now();

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class findAllByRunnerId는 {

        @Test
        @DisplayName("모든 싱글 데이터를 조회한다.")
        void returnSingles() {
            final List<Single> singleData = List.of(
                    new Single(runnerId, new RunningTime(0, 0, 1), List.of(new RunnerRecord(GpsData.of(1, 1, 1, now), 0))),
                    new Single(runnerId, new RunningTime(0, 0, 1), List.of(new RunnerRecord(GpsData.of(1, 1, 1, now), 0)))
            );

            singleRepository.saveAll(singleData);

            final List<Single> singleResult = singleRepository.findAllByRunnerId(runnerId);

            assertThat(singleResult).hasSize(2);
        }
    }
}
