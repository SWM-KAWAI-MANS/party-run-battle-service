package online.partyrun.partyrunbattleservice.domain.record.entity;

public interface DistanceCalculator<T, U> {
    U calculate(T point1, T point2);
}
