package online.partyrun.partyrunbattleservice.domain.runner.entity.record;

public interface DistanceCalculator<T, U> {
    U calculate(T point1, T point2);
}
