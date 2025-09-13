package com.sean.ninnong.member.controller;

import com.sean.ninnong.auth.security.UserPrincipal;
import com.sean.ninnong.member.dto.MemberInfo;
import com.sean.ninnong.member.service.MemberService;
import com.sean.ninnong.member.dto.TeamMemberRequest;
import com.sean.ninnong.member.dto.MemberResponseMsg;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping
    public ResponseEntity<MemberResponseMsg> create(@RequestBody TeamMemberRequest request) {
        memberService.add(request.getTeamId(), request.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponseMsg.addFrom(request.getUserId()));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<List<MemberInfo>> getMemberList(@PathVariable Long teamId) {

        return ResponseEntity.ok().body(memberService.getMemberList(teamId));
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<MemberResponseMsg> updateMembersInfo(@PathVariable Long teamId, @RequestBody List<MemberInfo> updateMembersInfo, @AuthenticationPrincipal UserPrincipal user) {
        memberService.updateMembersInfo(teamId, updateMembersInfo, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(MemberResponseMsg.updateFrom(teamId));
    }

}
