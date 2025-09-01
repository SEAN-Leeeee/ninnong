package com.sean.ninnong.team;

import com.sean.ninnong.exception.UnauthorizedTeamAccessException;
import com.sean.ninnong.team.dto.TeamInfoRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long creator;
    @Column(nullable = false)
    private Long leader;
    private String meetingDay;
    @Column(nullable = false)
    private String region;
    private int membershipFee;
    private String logo;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean isRecruitingMembers;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private TeamStatus isDeleted;
    private LocalDateTime deletedAt;

    public Team(TeamInfoRequest teamInfo, Long creatorId){
        this.creator = creatorId;
        this.leader = creatorId;
        this.region = teamInfo.getRegion();
        this.name = teamInfo.getName();
        this.meetingDay = teamInfo.getMeetingDay();
        this.membershipFee = teamInfo.getMembershipFee();
        this.logo = teamInfo.getLogo();
        this.description = teamInfo.getDescription();
        this.isRecruitingMembers = teamInfo.getIsRecruitingMembers();
        this.createdAt = LocalDateTime.now();
        this.isDeleted = TeamStatus.ACTIVE;
        this.deletedAt = null;

    }
    public static Team createTeam(TeamInfoRequest teamInfo, Long creatorId){
       return new Team(teamInfo, creatorId);
    }

    public void softDeleteTeam() {
        this.isDeleted = TeamStatus.DELETED;
    }

    public String getName() {
        return this.name;
    }

    public enum TeamStatus { ACTIVE, DELETED}

    public void updateInfo(TeamInfoRequest request) {
        this.name = request.getName();
        this.region = request.getRegion();
        this.logo = request.getLogo();
        this.description = request.getDescription();
    }
    public void updateLeader(Long id) {this.leader = id;}

    public void validateAuthorization(Long userId) {
        if (!this.leader.equals(userId)) {
            throw new UnauthorizedTeamAccessException(this.id);
        }
    }

    public Boolean getRecruitingMembers() {
        return isRecruitingMembers;
    }

    public String getMeetingDay() {
        return meetingDay;
    }

    public int getMembershipFee() {
        return membershipFee;
    }

    public String getLogo() {
        return logo;
    }

    public String getDescription() {
        return description;
    }

    public String getRegion() {
        return region;
    }


    public Long getId() {
        return this.id;
    }

}
