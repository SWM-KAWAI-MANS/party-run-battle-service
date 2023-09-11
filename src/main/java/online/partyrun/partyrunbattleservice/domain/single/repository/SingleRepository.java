package online.partyrun.partyrunbattleservice.domain.single.repository;

import online.partyrun.partyrunbattleservice.domain.single.entity.Single;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SingleRepository extends MongoRepository<Single, String> {
}
