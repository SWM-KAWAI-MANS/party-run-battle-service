package online.partyrun.partyrunbattleservice.domain.member.service;

import static online.partyrun.partyrunbattleservice.fixture.MemberFixture.멤버_박성우;
import static online.partyrun.partyrunbattleservice.fixture.MemberFixture.멤버_박현준;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import online.partyrun.partyrunbattleservice.domain.member.entity.Member;
import online.partyrun.partyrunbattleservice.domain.member.repository.MemberRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@DisplayName("MemberService")
class MemberServiceTest {

    @Autowired MemberRepository memberRepository;

    @Autowired MemberService memberService;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 멤버를_찾을_때 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 존재하는_멤버들의_아이디를_받으면 {
            List<String> 멤버_ids =
                    List.of(
                            memberRepository.save(멤버_박성우).getId(),
                            memberRepository.save(멤버_박현준).getId());

            @Test
            @DisplayName("멤버를 반환한다.")
            void returnMembers() {
                List<Member> members = memberService.findMembers(멤버_ids);
                assertThat(members).usingRecursiveComparison().isEqualTo(List.of(멤버_박성우, 멤버_박현준));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 존재하지_않는_아이디를_받으면 {
            List<String> invalidIds = List.of("장세연", "이승열");

            @Test
            @DisplayName("예외를 던진다.")
            void throwExceptions() {
                assertThatThrownBy(() -> memberService.findMembers(invalidIds))
                        .isInstanceOf(InvalidMemberException.class);
            }
        }
    }
}