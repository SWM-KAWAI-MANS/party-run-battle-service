package online.partyrun.partyrunbattleservice.domain.member.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.member.entity.Member;
import online.partyrun.partyrunbattleservice.domain.member.exception.MemberNotFoundException;
import online.partyrun.partyrunbattleservice.domain.member.repository.MemberRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberService {
    MemberRepository memberRepository;

    public List<Member> findMembers(List<String> memberIds) {
        final List<Member> members = memberRepository.findAllById(memberIds);
        validateMembers(members, memberIds);
        return members;
    }

    private void validateMembers(List<Member> members, List<String> memberIds) {
        if (members.size() != memberIds.size()) {
            throw new InvalidMemberException(memberIds);
        }
    }

    public void save(String memberId) {
        final Member member = new Member(memberId);
        memberRepository.save(member);
    }

    public void delete(String memberId) {
        final Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));
        memberRepository.delete(member);
    }
}
