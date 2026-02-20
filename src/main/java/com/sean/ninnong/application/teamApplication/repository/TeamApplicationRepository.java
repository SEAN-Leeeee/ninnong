package com.sean.ninnong.application.teamApplication.repository;


import com.sean.ninnong.application.teamApplication.domain.TeamApplication;
import com.sean.ninnong.application.teamApplication.dto.UserApplication;
import com.sean.ninnong.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamApplicationRepository extends JpaRepository<TeamApplication, Long> {

    Optional<TeamApplication> findByTeamIdAndApplicantAndStatus(Long teamId, Long application, ApplicationStatus status);

    Optional<TeamApplication> findByApplicantAndStatus(Long application, ApplicationStatus status);

    @Query("SELECT new com.sean.ninnong.application.teamApplication.dto.UserApplication(u.name, u.draftLevel, ta.requestMsg, ta.requestAt, ta.status) " +
            "FROM TeamApplication ta, User u " +
            "WHERE ta.applicant = u.id " +
            "AND ta.teamId = :teamId")
    List<UserApplication> findUserApplicationsByTeamId(@Param("teamId") Long teamId);
    List<TeamApplication> findApplicationsByTeamId(@Param("teamId") Long teamId);


}
