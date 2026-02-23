package com.sean.ninnong.member.repository;


import com.sean.ninnong.common.enums.MemberStatus;
import com.sean.ninnong.member.domain.Member;
import com.sean.ninnong.member.repository.projection.TeamMemberCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByUser_IdAndTeam_Id(Long userId, Long teamId);

    int countByTeam_Id(Long teamId);

    @Query("SELECT m.team.id AS teamId, COUNT(m) AS count FROM Member m GROUP BY m.team.id")
    List<TeamMemberCount> countGroupedByTeamId();

    @Transactional(readOnly = true)
    @Query("SELECT m.team.id FROM Member m WHERE m.user.id = :userId")
    Long findTeamIdByUserId(@Param("userId") Long userId);


    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.user " +
            "WHERE m.team.id = :teamId " +
            "AND m.status = :status " +
            "ORDER BY m.backNumber")
    List<Member> findByTeam_IdAndStatusOrderByBackNumber(Long teamId, MemberStatus active);

    List<Member> findByIdIn(List<Long> memberIdList);

}
