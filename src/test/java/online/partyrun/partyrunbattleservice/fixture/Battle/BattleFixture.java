package online.partyrun.partyrunbattleservice.fixture.Battle;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import java.util.List;

public class BattleFixture {
    public static final Runner 박성우 = new Runner("박성우");
    public static final Runner 박현준 = new Runner("박현준");
    public static final Runner 노준혁 = new Runner("노준혁");
    public static final Battle 배틀1 = new Battle(1000, List.of(박성우, 박현준, 노준혁));
}
