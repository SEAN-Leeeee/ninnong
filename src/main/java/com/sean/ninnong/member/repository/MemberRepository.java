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


    Optional<Member> findByUserIdAndTeamId(Long userId, Long teamId);

    int countByTeamId(Long teamId);

    @Query("SELECT m.teamId AS teamId, COUNT(m) AS count FROM Member m GROUP BY m.teamId")
    List<TeamMemberCount> countGroupedByTeamId();

    @Transactional(readOnly = true)
    @Query("SELECT m.teamId FROM Member m WHERE m.userId = :userId")
    Long findTeamIdByUserId(@Param("userId") Long userId);

    List<Member> findByTeamIdAndStatusOrderByBackNumber(Long teamId, MemberStatus active);

    List<Member> findByIdIn(List<Long> memberIdList);

}
