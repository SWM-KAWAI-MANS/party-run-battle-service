package online.partyrun.partyrunbattleservice.domain.member.repository;

import online.partyrun.partyrunbattleservice.domain.member.entity.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepository extends MongoRepository<Member, String> {
}
