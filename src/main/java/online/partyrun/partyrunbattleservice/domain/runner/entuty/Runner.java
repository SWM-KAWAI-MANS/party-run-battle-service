package online.partyrun.partyrunbattleservice.domain.runner.entuty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Runner {
    @Id
    String id;

    String name;

    public Runner(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
