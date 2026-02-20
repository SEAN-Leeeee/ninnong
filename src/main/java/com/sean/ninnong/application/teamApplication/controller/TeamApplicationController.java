package com.sean.ninnong.application.teamApplication.controller;

import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.application.dto.ApplicationResponseMsg;
import com.sean.ninnong.application.teamApplication.service.TeamApplicationService;
import com.sean.ninnong.application.teamApplication.dto.ApplicationDecisionRequest;
import com.sean.ninnong.application.teamApplication.dto.UserApplication;
import com.sean.ninnong.auth.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teamApplication")
public class TeamApplicationController {

    private final TeamApplicationService teamApplicationService;

    public TeamApplicationController(TeamApplicationService teamApplicationService) {
        this.teamApplicationService = teamApplicationService;
    }

    @PostMapping("/{teamId}")
    public ResponseEntity<ApplicationResponseMsg> apply(@PathVariable Long teamId, @RequestBody ApplicationRequest request, @AuthenticationPrincipal UserPrincipal user) {
        teamApplicationService.applyWith(teamId, request, user.getId());

        return ResponseEntity.ok(ApplicationResponseMsg.submittedApplication(teamId));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<ApplicationResponseMsg> cancelApplication(@PathVariable Long teamId, @AuthenticationPrincipal UserPrincipal user) {
        teamApplicationService.cancelApply(teamId, user.getId());
        return ResponseEntity.ok(ApplicationResponseMsg.cancelApply(teamId));
    }


    @PatchMapping("/{teamId}")
    public ResponseEntity<ApplicationResponseMsg> responseToApplication(@PathVariable Long teamId, @RequestBody ApplicationDecisionRequest request, @AuthenticationPrincipal UserPrincipal user) {
        teamApplicationService.responseTo(teamId, request, user.getId());

        return ResponseEntity.ok(ApplicationResponseMsg.applyOf(teamId, request.getDecision()));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApplicationResponse> findMyApplication(@AuthenticationPrincipal UserPrincipal user) {
        ApplicationResponse myApplication = teamApplicationService.findMyApplication(user.getId());

        return ResponseEntity.ok(myApplication);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<List<UserApplication>> getTeamApplications(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long teamId) {
        List<UserApplication> response = teamApplicationService.getTeamApplications(teamId);

        return ResponseEntity.ok(response);
    }
}
