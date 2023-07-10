package online.partyrun.partyrunbattleservice.domain.runner.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.member.entity.Member;
import online.partyrun.partyrunbattleservice.domain.member.service.MemberService;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunnerService {

    MemberService memberService;

    public List<Runner> findAllById(List<String> runnerIds) {
        final List<Member> members = memberService.findMembers(runnerIds);

        return members.stream().map(member -> new Runner(member.getId())).toList();
    }
}
