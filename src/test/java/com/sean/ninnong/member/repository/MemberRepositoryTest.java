package com.sean.ninnong.member.repository;

import com.sean.ninnong.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("저장되어있는 유저 아이디를 넣으면 들어가있는 팀의 id가 조회된다.")
    @Test
    void findTeamWithUserId() {
        //given
        Member member = Member.create(1L, 1L);
        memberRepository.save(member);
        memberRepository.flush();

        //when
        Long id = memberRepository.findTeamIdByUserId(1L);

        //then
        assertThat(id).isEqualTo(1);
    }
}
