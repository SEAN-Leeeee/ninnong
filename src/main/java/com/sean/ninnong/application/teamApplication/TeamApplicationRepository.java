package com.sean.ninnong.application.teamApplication;


import com.sean.ninnong.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamApplicationRepository extends JpaRepository<TeamApplication, Long> {

    Optional<TeamApplication> findByTeamIdAndApplicantAndStatus(Long teamId, Long application, ApplicationStatus status);
    Optional<TeamApplication> findByApplicantAndStatus(Long application, ApplicationStatus status);

    List<TeamApplication> findByTeamIdAndStatus(Long teamId, ApplicationStatus pending);
}
