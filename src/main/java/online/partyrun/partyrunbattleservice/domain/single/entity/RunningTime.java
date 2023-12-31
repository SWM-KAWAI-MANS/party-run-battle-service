package online.partyrun.partyrunbattleservice.domain.single.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.single.exception.IllegalRunningTimeException;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunningTime {

    private static final int MIN_TIME = 0;
    private static final int MAX_TIME = 59;

    int hours;
    int minutes;
    int seconds;

    public RunningTime(int hours, int minutes, int seconds) {
        validateNoTime(hours, minutes, seconds);
        validateIsCorrectTime(hours, minutes, seconds);


        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    private void validateNoTime(int hours, int minutes, int seconds) {
        if (hours == MIN_TIME && minutes == MIN_TIME && seconds == MIN_TIME) {
            throw new IllegalRunningTimeException(hours, minutes, seconds);
        }
    }

    private void validateIsCorrectTime(int hours, int minutes, int seconds) {
        if (isNotCorrectHours(hours) || isNotCorrectMinutes(minutes) || isNotCorrectSeconds(seconds)) {
            throw new IllegalRunningTimeException(hours, minutes, seconds);
        }
    }

    private boolean isNotCorrectHours(int hours) {
        return MIN_TIME > hours;
    }

    private boolean isNotCorrectMinutes(int minutes) {
        return minutes < MIN_TIME || minutes > MAX_TIME;
    }

    private boolean isNotCorrectSeconds(int seconds) {
        return seconds < MIN_TIME || seconds > MAX_TIME;
    }

    public int getTotalSeconds() {
        return hours * 60 * 60 + minutes * 60 + seconds;
    }
}
