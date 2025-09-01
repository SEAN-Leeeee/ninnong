package com.sean.ninnong.team;

import com.sean.ninnong.auth.security.UserPrincipal;
import com.sean.ninnong.team.dto.TeamInfoRequest;
import com.sean.ninnong.team.dto.TeamResponse;
import com.sean.ninnong.team.dto.TeamMsgResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {


    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamMsgResponse> createTeam(@RequestBody TeamInfoRequest request, @AuthenticationPrincipal UserPrincipal user) {
       Long teamId = teamService.createTeam(request, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(TeamMsgResponse.createFrom(teamId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        TeamResponse team = teamService.getTeam(id);

        return ResponseEntity.ok(team);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeamList() { /*여기에 팀 공개/비공개를 넣어야될까 ? */
        List<TeamResponse> teamList = teamService.getTeamList();
        return ResponseEntity.ok(teamList);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TeamMsgResponse> updateTeamInfo(@RequestBody TeamInfoRequest request,
                                                          @PathVariable Long id,
                                                          @AuthenticationPrincipal UserPrincipal user) {
        teamService.updateOf(id, request, user.getId());
        return ResponseEntity.ok(TeamMsgResponse.updateFrom(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TeamMsgResponse> deleteTeam(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserPrincipal user) {
        teamService.softDeleteTeam(id, user.getId());
        return ResponseEntity.ok(TeamMsgResponse.deleteFrom(id));
    }




}
