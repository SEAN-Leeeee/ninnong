package com.sean.ninnong.member;


import com.sean.ninnong.common.enums.Role;
import com.sean.ninnong.exception.UnauthorizedTeamAccessException;
import com.sean.ninnong.member.repository.MemberRepository;
import com.sean.ninnong.user.UserInfo;
import com.sean.ninnong.user.UserReader;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService, MemberReader {
    private final MemberRepository memberRepository;
    private final UserReader userReader;

    public MemberServiceImpl(MemberRepository memberRepository, UserReader userReader) {
        this.memberRepository = memberRepository;
        this.userReader = userReader;
    }

    @Override
    public Member of(Long teamId, Long userId) {
        Member member = Member.of(teamId, userId);
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
        List<Member> memberList = memberRepository.findByTeamIdAndStatusOrderByBackNumber(teamId, Member.Status.ACTIVE);

        List<Long> memberIdList = memberList.stream()
                .map(Member::getUserId).toList();

        List<UserInfo> userInfoList = userReader.getUserInfoList(memberIdList);

        Map<Long, UserInfo> userInfoMap = userInfoList.stream()
                                                .collect(Collectors.toMap(UserInfo::getId, userInfo -> userInfo));

        return memberList.stream()
                .map(member -> {
                    UserInfo userInfo = userInfoMap.get(member.getUserId());
                    if (userInfo == null) {
                        throw new IllegalStateException("UserInfo is missing for userId = " + member.getUserId());
                    }
                    return MemberInfo.of(member, userInfo);
                })
                .toList();

    }

    @Transactional
    @Override
    public void updateMembersInfo(Long teamId, List<MemberInfo> updateMembersInfo, Long userId) {
        Member editor = memberRepository.findByUserIdAndTeamId(userId, teamId)
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
