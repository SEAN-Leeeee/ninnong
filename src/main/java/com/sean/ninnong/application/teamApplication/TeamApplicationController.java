package com.sean.ninnong.application.teamApplication;

import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.application.dto.ApplicationResponseMsg;
import com.sean.ninnong.auth.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        teamApplicationService.cancelApplication(teamId, user.getId());
        return ResponseEntity.ok(ApplicationResponseMsg.cancelApply(teamId));
    }


    @PatchMapping("/{teamId}")
    public ResponseEntity<ApplicationResponseMsg> respondToApplication(@PathVariable Long teamId, @RequestBody ApplicationDecisionRequest request) {
        teamApplicationService.respondTo(teamId, request.getApplicantId(), request.getDecision());

        return ResponseEntity.ok(ApplicationResponseMsg.applyOf(teamId, request.getDecision()));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApplicationResponseMsg> getPendingApplicationTeamIdByUserId(@AuthenticationPrincipal UserPrincipal user) {
        Long teamId = teamApplicationService.getPendingApplicationTeamIdByUserId(user.getId());

        return ResponseEntity.ok(ApplicationResponseMsg.hasApplied(teamId));
    }
}
