package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunnerRecord {

    @Id String id;
    String battleId;
    String runnerId;
    List<Record> records;

    public RunnerRecord(String battleId, String runnerId) {
        this.battleId = battleId;
        this.runnerId = runnerId;
        this.records = new ArrayList<>();
    }
}
