package com.sean.ninnong.member.service;


import com.sean.ninnong.common.enums.Role;
import com.sean.ninnong.exception.UnauthorizedTeamAccessException;
import com.sean.ninnong.member.domain.Member;
import com.sean.ninnong.member.dto.MemberInfo;
import com.sean.ninnong.member.repository.MemberRepository;
import com.sean.ninnong.user.domain.User;
import com.sean.ninnong.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sean.ninnong.common.enums.MemberStatus.ACTIVE;

@Service
public class MemberServiceImpl implements MemberService, MemberReader {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public MemberServiceImpl(MemberRepository memberRepository, UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Member add(Long teamId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
        Member member = Member.create(teamId, user);
        memberRepository.save(member);

        return member;
    }

    @Override
    public Long getMyTeamId(Long userId) {
        Long teamId = memberRepository.findTeamIdByUserId(userId);
        return (teamId != null ? teamId : 0L);
    }

    @Override
    public List<MemberInfo> getMemberList(Long teamId) {
        List<Member> memberList = memberRepository.findByTeamIdAndStatusOrderByBackNumber(teamId, ACTIVE);

        return memberList.stream()
                .map(MemberInfo::from)
                .toList();
    }


    @Transactional
    @Override
    public void updateMembersInfo(Long teamId, List<MemberInfo> updateMembersInfo, Long userId) {
        Member editor = memberRepository.findByUser_IdAndTeamId(userId, teamId)
                .orElseThrow(() -> new UnauthorizedTeamAccessException(teamId));

        if (editor.getRole() != Role.LEADER) {
            throw new AccessDeniedException("리더 권한이 없습니다.");
        }

        List<Long> memberIdList = updateMembersInfo.stream()
                .map(MemberInfo::userId).toList();
        List<Member> memberList = memberRepository.findByIdIn(memberIdList);


        Map<Long, MemberInfo> memberInfos = updateMembersInfo.stream()
                                                        .collect(Collectors.toMap(MemberInfo::userId, memberInfo -> memberInfo));
        memberList.forEach(member -> {
            MemberInfo info = memberInfos.get(member.getUserId());
            member.updateFrom(info);
        });


    }
}
