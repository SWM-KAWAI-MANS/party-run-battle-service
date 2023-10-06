package online.partyrun.partyrunbattleservice.domain.single.repository;

import online.partyrun.partyrunbattleservice.domain.single.entity.Single;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SingleRepository extends MongoRepository<Single, String> {

    List<Single> findAllByRunnerId(String memberId);
}
