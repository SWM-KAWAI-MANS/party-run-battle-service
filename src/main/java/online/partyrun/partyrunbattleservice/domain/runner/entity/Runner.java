package online.partyrun.partyrunbattleservice.domain.runner.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.Id;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Runner {
    @Id String id;
    RunnerStatus status = RunnerStatus.READY;

    public Runner(String id) {
        this.id = id;
    }
}
