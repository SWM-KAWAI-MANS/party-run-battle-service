package online.partyrun.partyrunbattleservice.domain.battle.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Battle {
    @Id String id;
    List<Runner> runners;
    BattleStatus status = BattleStatus.READY;
    @CreatedDate LocalDateTime createdAt;

    public Battle(List<Runner> runners) {
        this.runners = runners;
    }

    public void changeStatus(BattleStatus status) {
        this.status = Objects.requireNonNull(status);
    }
}
