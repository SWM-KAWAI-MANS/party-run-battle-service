package online.partyrun.partyrunbattleservice.domain.battle.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.runner.entuty.Runner;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Battle {
    @Id
    String id;

    List<Runner> runners;

    public Battle(List<Runner> runners) {
        this.runners = runners;
    }
}
