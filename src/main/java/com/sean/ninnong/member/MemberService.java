package com.sean.ninnong.member;


import java.util.List;

public interface MemberService {

    Member of(Long teamId, Long userId);

    List<MemberInfo> getMemberList(Long teamId);

    void updateMembersInfo(Long teamId, List<MemberInfo> updateMembersInfo, Long userId);
}
