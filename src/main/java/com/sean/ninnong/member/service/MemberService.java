package com.sean.ninnong.member.service;


import com.sean.ninnong.member.domain.Member;
import com.sean.ninnong.member.dto.MemberInfo;
import com.sean.ninnong.user.domain.User;

import java.util.List;

public interface MemberService {

    Member add(Long teamId,  Long userId);

    List<MemberInfo> getMemberList(Long teamId);

    void updateMembersInfo(Long teamId, List<MemberInfo> updateMembersInfo, Long userId);
}
