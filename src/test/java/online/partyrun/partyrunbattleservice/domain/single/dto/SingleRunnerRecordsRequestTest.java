package online.partyrun.partyrunbattleservice.domain.single.dto;

import online.partyrun.partyrunbattleservice.domain.runner.entity.record.RunnerRecord;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("SingleRunnerRecordsRequest")
class SingleRunnerRecordsRequestTest {

    @Test
    @DisplayName("records에서 runnerRecords를 가져온다")
    void getRunnerRecords() {
        LocalDateTime now = LocalDateTime.now();
        final List<SingleRunnerRecordRequest> singleRunnerRecords = List.of(
                new SingleRunnerRecordRequest(0, 0, 0, now, 0),
                new SingleRunnerRecordRequest(0.0001, 0.0001, 0.0001, now, 1)
        );
        SingleRunnerRecordsRequest request = new SingleRunnerRecordsRequest(
                new RunningTimeRequest(0,0,1),
                singleRunnerRecords
        );
        final List<RunnerRecord> runnerRecords = request.runnerRecords();
        assertThat(runnerRecords).hasSize(singleRunnerRecords.size());
    }
}