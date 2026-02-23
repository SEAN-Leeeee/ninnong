import com.sean.ninnong.member.domain.Member;
import com.sean.ninnong.team.domain.Team;
import com.sean.ninnong.team.dto.TeamInfoRequest;
import com.sean.ninnong.team.repository.TeamRepository;
import com.sean.ninnong.user.domain.User;
import com.sean.ninnong.user.repository.UserRepository;
import com.sean.ninnong.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sean.ninnong.auth.dto.RegisterRequest;
import com.sean.ninnong.common.enums.DraftLevel;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    UserRepository userRepository; // Added
    @Autowired
    TeamRepository teamRepository; // Added

    @DisplayName("저장되어있는 유저 아이디를 넣으면 들어가있는 팀의 id가 조회된다.")
    @Test
    void findTeamWithUserId() {
        //given
        RegisterRequest registerRequest = new RegisterRequest("test@test.com", "name", "nickname", "password", DraftLevel.NONE);
        User user = User.create(registerRequest, "encodedPassword"); // Use User's static factory method
        userRepository.save(user); // Save the User

        TeamInfoRequest mockTeamInfoRequest = new TeamInfoRequest() {
            @Override
            public String getName() { return "Test Team Name"; }
            @Override
            public String getRegion() { return "Test Region"; }
            @Override
            public Boolean getIsRecruitingMembers() { return true; }
            @Override
            public String getLogo() { return null; }
            @Override
            public String getMeetingDay() { return null; }
            @Override
            public int getMembershipFee() { return 0; }
            @Override
            public String getDescription() { return "Test Description"; }
        };
        Team team = Team.createTeam(mockTeamInfoRequest, user); // Create a Team
        teamRepository.save(team); // Save the Team

        Member member = Member.create(team, user); // Pass Team and User objects
        memberRepository.save(member);
        memberRepository.flush();

        //when
        Long id = memberRepository.findTeamIdByUserId(user.getId()); // Use user.getId()

        //then
        assertThat(id).isEqualTo(team.getId()); // Assert with team.getId()
    }
}
