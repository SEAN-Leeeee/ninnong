package com.sean.ninnong.member.domain;

import com.sean.ninnong.common.enums.MemberPosition;
import com.sean.ninnong.common.enums.MemberStatus;
import com.sean.ninnong.common.enums.Role;
import com.sean.ninnong.member.dto.MemberInfo;
import com.sean.ninnong.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private int backNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberPosition position;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;
    @Column(nullable = false)
    private LocalDateTime joinedAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public Member(Long teamId, User user) {
        this.teamId = teamId;
        this.user = user;
        this.backNumber = 0;
        this.position = MemberPosition.NONE;
        this.role = Role.MEMBER;
        this.joinedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.status = MemberStatus.ACTIVE;
        this.deletedAt = null;
    }

    public static Member create(Long teamId, User user) {
        return new Member(teamId, user);
    }


    public void asLeader() {
        this.role = Role.LEADER;
    }

    public void asMember() {
        this.role = Role.MEMBER;
    }

    public void stop() {
        this.status = MemberStatus.STOPPED;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateStatus(MemberStatus status) {
        this.status = status;
    }

    public void updateFrom(MemberInfo memberInfo) {
        backNumber = memberInfo.backNumber();
        position = memberInfo.position();
        joinedAt = memberInfo.joinedAt();
    }

    public Long getUserId() {
        return user.getId();
    }

    public Role getRole() {
        return role;
    }

    public int getBackNumber() {
        return backNumber;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "@@ Member{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", userId=" + user.getId() +
                ", backNumber=" + backNumber +
                ", position=" + position +
                ", joinedAt=" + joinedAt +
                ", createdAt=" + createdAt +
                ", role=" + role +
                ", status=" + status +
                '}';
    }

    public MemberPosition getPosition() {
        return position;
    }
}
